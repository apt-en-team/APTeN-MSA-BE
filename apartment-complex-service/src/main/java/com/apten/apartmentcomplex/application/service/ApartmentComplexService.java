package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexMapper;
import com.apten.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;

import com.apten.common.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
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
                .addressDetail(req.getDetailAddress())
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

    // 관리자 단지 목록 조회 서비스 API-202 mb
    public ApartmentComplexGetPageRes getApartmentComplexList(ApartmentComplexSearchReq req) {
        // TODO: 단지 목록 조회 로직 구현
        return ApartmentComplexGetPageRes.empty(req.getPage(), req.getSize());
    }

    // 관리자 단지 상세 조회 서비스 API-203 mb
    public ApartmentComplexGetDetailRes getApartmentComplexDetail(String ComplexUid) {
        // TODO: 단지 상세 조회 로직 구현
        return ApartmentComplexGetDetailRes.builder().build();
    }

    // 단지 수정 서비스 API-204
    @Transactional
    public ApartmentComplexPatchRes updateApartmentComplex(String ComplexUid, ApartmentComplexReq req) {
        // API의 단지 UID를 엔티티 code로 보고 수정 대상 단지를 조회한다
        ApartmentComplex apartmentComplex = apartmentComplexRepository.findByCode(ComplexUid)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 단지 원본 정보를 갱신하되 상태 변경 로직은 별도 API가 생길 때 분리한다
        apartmentComplex.update(
                req.getName(),
                req.getAddress(),
                req.getDetailAddress(),
                req.getZipcode(),
                apartmentComplex.getStatus(),
                null
        );

        // Kafka 직접 발행 대신 수정 이벤트를 같은 트랜잭션 안에서 Outbox에 적재한다
        apartmentComplexOutboxService.saveUpdatedEvent(apartmentComplex);

        return ApartmentComplexPatchRes.builder()
                .apartmentComplexUid(ComplexUid)
                .name(apartmentComplex.getName())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 단지 소속 지정 서비스 API-205
    public ComplexAdminPostRes assignAdminToComplex(String ComplexUid, ComplexAdminPostReq req) {
        // TODO: 관리자 단지 소속 지정 로직 구현
        return ComplexAdminPostRes.builder()
                .apartmentComplexUid(ComplexUid)
                .userUid(req.getUserUid())
                .adminRole(req.getAdminRole())
                .assignedAt(LocalDateTime.now())
                .build();
    }

    // 공개 단지 목록 조회 서비스 API-214 mb
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // TODO: 가입 화면용 공개 단지 목록 조회 로직 구현
        return List.of();
    }
}
