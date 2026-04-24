package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.*;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import com.apten.apartmentcomplex.domain.entity.UserCache;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexAdminRepository;
import com.apten.apartmentcomplex.domain.repository.UserCacheRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexMapper;
import com.apten.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;

import com.apten.common.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지 도메인 응용 서비스
// 단지 등록, 조회, 수정과 관리자 소속 지정을 이 서비스가 묶는다
@Service
@RequiredArgsConstructor
public class ApartmentComplexService {

    private final ApartmentComplexRepository apartmentComplexRepository;
    private final ObjectProvider<ApartmentComplexMapper> apartmentComplexMapper;
    private final ApartmentComplexOutboxService apartmentComplexOutboxService;
    private final UserCacheRepository userCacheRepository;
    private final ComplexAdminRepository complexAdminRepository;

    //최소정보 체크
    private void validateCreateApartmentComplexReq(ApartmentComplexReq req) {
        if (req == null
                || isBlank(req.getName())
                || isBlank(req.getAddress())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String generateNextComplexCode() {
        return apartmentComplexRepository.findLastCode()
                .map(code -> increaseComplexCode(code))
                .orElse("APT-0001");
    }

    private String increaseComplexCode(String lastCode) {
        int number = Integer.parseInt(lastCode.substring(4));
        return String.format("APT-%04d", number + 1);
    }

    // 단지 등록 서비스 API-201
    @Transactional
    public ApartmentComplexPostRes createApartmentComplex(ApartmentComplexReq req) {

        validateCreateApartmentComplexReq(req);
        //중복 체크
        if (apartmentComplexRepository.existsByName(req.getName())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX);
        }

        String code = generateNextComplexCode();

        // 단지 원본을 먼저 저장해 aggregateId로 사용할 Long PK를 확보한다
        ApartmentComplex apartmentComplex = ApartmentComplex.builder()
                .code(code)
                .name(req.getName())
                .address(req.getAddress())
                .addressDetail(req.getAddressDetail())
                .zipCode(req.getZipcode())
                .status(ApartmentComplexStatus.ACTIVE)
                .description(req.getDescription())
                .build();
        ApartmentComplex savedApartmentComplex = apartmentComplexRepository.save(apartmentComplex);

        // Kafka 전송은 relay가 담당하므로 같은 트랜잭션 안에서는 Outbox row만 남긴다
        apartmentComplexOutboxService.saveCreatedEvent(savedApartmentComplex);

        return ApartmentComplexPostRes.builder()
                .complexId(savedApartmentComplex.getId())
                .code(savedApartmentComplex.getCode())
                .name(savedApartmentComplex.getName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 단지 목록 조회 서비스 API-202
    @Transactional(readOnly = true)
    public ApartmentComplexGetPageRes getApartmentComplexList(ApartmentComplexSearchReq req) {
        // 요청값이 없을 때 기본 페이지와 사이즈를 설정한다.
        int page = req.getPage();
        int size = req.getSize();

        // JPA 페이징 처리를 위한 PageRequest를 생성한다.
        PageRequest pageRequest = PageRequest.of(page, size);

        // 키워드 조건을 적용해 단지 목록을 페이지 단위로 조회한다.
        Page<ApartmentComplex> result = apartmentComplexRepository.findPageByKeyword(
                req.getKeyword(),
                pageRequest
        );

        // 조회된 엔티티 목록을 API 응답 DTO 목록으로 변환한다.
        List<ApartmentComplexGetRes> content = result.getContent()
                .stream()
                .map(complex -> ApartmentComplexGetRes.builder()
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .status(complex.getStatus())
                        .description(complex.getDescription())
                        .createdAt(complex.getCreatedAt())
                        .build())
                .toList();

        // 페이지 메타데이터와 목록 데이터를 함께 응답한다.
        return ApartmentComplexGetPageRes.builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .build();
    }

    // 관리자 단지 상세 조회 서비스 API-203
    public ApartmentComplexGetDetailRes getApartmentComplexDetail(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));
        return ApartmentComplexGetDetailRes.builder()
                .apartmentComplexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .address(complex.getAddress())
                .addressDetail(complex.getAddressDetail())
                .zipcode(complex.getZipCode())
                .status(complex.getStatus())
                .description(complex.getDescription())
                .createdAt(complex.getCreatedAt())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 단지 수정 서비스 API-204
    @Transactional
    public ApartmentComplexPatchRes updateApartmentComplex(String code, ApartmentComplexReq req) {
        // API의 단지 code로 보고 수정 대상 단지를 조회한다
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 단지 원본 정보를 갱신하되 상태 변경 로직은 별도 API가 생길 때 분리한다
        complex.update(
                req.getName(),
                req.getAddress(),
                req.getAddressDetail(),
                req.getZipcode(),
                complex.getStatus(),
                req.getDescription()
        );

        // Kafka 직접 발행 대신 수정 이벤트를 같은 트랜잭션 안에서 Outbox에 적재한다
        apartmentComplexOutboxService.saveUpdatedEvent(complex);

        return ApartmentComplexPatchRes.builder()
                .code(code)
                .name(complex.getName())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 관리자 단지 소속 지정 서비스 API-205
    public ComplexAdminPostRes assignAdminToComplex(String code, ComplexAdminPostReq req) {
        //단지검색
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        //유저검색
        UserCache userCache = userCacheRepository.findById(req.getUserId())
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.USER_NOT_FOUND));

        ComplexAdmin admin = new ComplexAdmin();
        admin.setComplexId(complex.getId());
        admin.setAdminUserId(userCache.getId());
        admin.setAdminName(userCache.getName());
        admin.setAdminRole(req.getAdminRole());
        admin.setIsActive(true);
        admin.setAssignedAt(LocalDateTime.now());

        complexAdminRepository.save(admin);

        return ComplexAdminPostRes.builder()
                .code(code)
                .name(admin.getAdminName())
                .adminRole(admin.getAdminRole())
                .assignedAt(admin.getAssignedAt())
                .build();
    }

    // 공개 단지 목록 조회 서비스 API-214 mb
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // TODO: 가입 화면용 공개 단지 목록 조회 로직 구현
        return List.of();
    }
}
