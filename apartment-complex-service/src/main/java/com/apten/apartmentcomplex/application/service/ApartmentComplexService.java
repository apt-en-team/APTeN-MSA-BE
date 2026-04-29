package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexStatusPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexStatusPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminDeleteRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminGetRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import com.apten.apartmentcomplex.domain.entity.UserCache;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.domain.enums.UserCacheRole;
import com.apten.apartmentcomplex.domain.enums.UserCacheStatus;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexAdminRepository;
import com.apten.apartmentcomplex.domain.repository.UserCacheRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexMapper;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지 도메인 응용 서비스이다.
// 단지 등록과 조회, 수정, 상태 변경, 관리자 배정 흐름을 이 서비스가 묶는다.
@Service
@Slf4j
@RequiredArgsConstructor
public class ApartmentComplexService {

    private final ApartmentComplexRepository apartmentComplexRepository;
    private final ObjectProvider<ApartmentComplexMapper> apartmentComplexMapper;
    private final ApartmentComplexOutboxService apartmentComplexOutboxService;
    private final UserCacheRepository userCacheRepository;
    private final ComplexAdminRepository complexAdminRepository;

    // 최소 생성 정보를 검증한다.
    private void validateCreateApartmentComplexReq(ApartmentComplexReq req) {
        if (req == null
                || isBlank(req.getName())
                || isBlank(req.getAddress())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // null 또는 blank 문자열인지 확인한다.
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // 다음 단지 코드를 만들기 위해 마지막 단지 코드를 조회한다.
    private String generateNextComplexCode() {
        return apartmentComplexRepository.findLastCode()
                .map(code -> increaseComplexCode(code))
                .orElse("APT-0001");
    }

    // 마지막 단지 코드 숫자를 1 증가시켜 새 코드를 만든다.
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

    // 관리자 단지 소속 지정 서비스 API-206이다.
    @Transactional
    public ComplexAdminPostRes assignAdminToComplex(String code, ComplexAdminPostReq req) {
        // 단지 코드로 배정 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 배정할 사용자 ID가 없으면 어느 사용자를 단지에 붙일지 알 수 없다.
        if (req == null || req.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // user_cache에서 실제 관리자 사용자 원본을 찾는다.
        UserCache userCache = userCacheRepository.findById(req.getUserId())
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.USER_NOT_FOUND));

        // 플랫폼 권한은 Auth Service가 관리하므로 ADMIN 계정만 단지에 배정할 수 있다.
        validateAdminUser(userCache);

        // 같은 단지에 대한 기존 배정 이력이 있으면 활성 상태에 따라 재사용한다.
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userCache.getId())
                .map(existingAdmin -> reactivateAdminAssignment(existingAdmin, userCache))
                .orElseGet(() -> ComplexAdmin.builder()
                        .complexId(complex.getId())
                        .adminUserId(userCache.getId())
                        .adminName(userCache.getName())
                        .isActive(true)
                        .assignedAt(LocalDateTime.now())
                        .build());

        // 관리자-단지 배정 여부만 저장하고 세부 역할은 Auth role에 맡긴다.
        complexAdminRepository.save(admin);

        return ComplexAdminPostRes.builder()
                .code(code)
                .userId(admin.getAdminUserId())
                .name(admin.getAdminName())
                .isActive(admin.getIsActive())
                .assignedAt(admin.getAssignedAt())
                .build();
    }

    // 단지 관리자 배정을 해제한다.
    @Transactional
    public ComplexAdminDeleteRes unassignAdminFromComplex(String code, Long userId) {
        // 단지 코드로 해제 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // complex_admin에서 현재 또는 과거 배정 이력을 조회한다.
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));

        // 이미 비활성 상태면 다시 해제할 수 없으므로 잘못된 요청으로 처리한다.
        if (!Boolean.TRUE.equals(admin.getIsActive())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 실제 삭제 대신 소프트 해제로 배정 상태만 끈다.
        admin.unassign();
        complexAdminRepository.save(admin);

        return ComplexAdminDeleteRes.builder()
                .code(code)
                .userId(admin.getAdminUserId())
                .isActive(admin.getIsActive())
                .unassignedAt(admin.getUnassignedAt())
                .build();
    }

    // 단지별 현재 활성 관리자 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<ComplexAdminGetRes> getComplexAdminList(String code) {
        // 단지 코드로 조회 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 현재 활성화된 관리자만 조회해 상세 화면에 내려준다.
        return complexAdminRepository.findByComplexIdAndIsActiveTrue(complex.getId()).stream()
                .map(admin -> ComplexAdminGetRes.builder()
                        .userId(admin.getAdminUserId())
                        .name(admin.getAdminName())
                        .isActive(admin.getIsActive())
                        .assignedAt(admin.getAssignedAt())
                        .unassignedAt(admin.getUnassignedAt())
                        .build())
                .toList();
    }

    // 단지 활성 상태를 별도 API에서 변경한다.
    @Transactional
    public ApartmentComplexStatusPatchRes changeApartmentComplexStatus(String code, ApartmentComplexStatusPatchReq req) {
        // 단지 코드로 상태 변경 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 바꿀 상태가 없으면 요청이 잘못된 것으로 본다.
        if (req == null || req.getStatus() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 상태를 별도 API에서만 바꾸도록 명시적으로 반영한다.
        complex.changeStatus(req.getStatus());

        // 단지 비활성화는 별도 이벤트로, 나머지 상태 변경은 일반 수정 이벤트로 적재한다.
        if (req.getStatus() == ApartmentComplexStatus.INACTIVE) {
            apartmentComplexOutboxService.saveDeactivatedEvent(complex);
        } else {
            apartmentComplexOutboxService.saveUpdatedEvent(complex);
        }

        return ApartmentComplexStatusPatchRes.builder()
                .code(complex.getCode())
                .status(complex.getStatus())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 공개 단지 목록 조회 서비스 API-209이다.
    @Transactional(readOnly = true)
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // 회원가입과 단지 선택 화면에는 활성 단지만 노출한다.
        return apartmentComplexRepository.findPublicListByKeyword(keyword, ApartmentComplexStatus.ACTIVE)
                .stream()
                .map(complex -> ApartmentComplexPublicRes.builder()
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .build())
                .toList();
    }

    // user_cache의 플랫폼 권한과 상태를 보고 단지 배정 가능한 ADMIN인지 검증한다.
    private void validateAdminUser(UserCache userCache) {
        // 단지 관리자 배정은 ADMIN 역할 사용자에게만 허용한다.
        if (userCache.getRole() != UserCacheRole.ADMIN) {
            throw new BusinessException(ApartmentComplexErrorCode.USER_NOT_ADMIN);
        }

        // 비활성 또는 삭제 상태 사용자는 단지 관리자로 배정하지 않는다.
        if (userCache.getStatus() != UserCacheStatus.ACTIVE || Boolean.TRUE.equals(userCache.getIsDeleted())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 기존 비활성 배정은 재활성화하고, 이미 활성 배정이면 중복으로 본다.
    private ComplexAdmin reactivateAdminAssignment(ComplexAdmin existingAdmin, UserCache userCache) {
        // 이미 활성화된 배정이면 같은 단지에 다시 배정할 수 없다.
        if (Boolean.TRUE.equals(existingAdmin.getIsActive())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX_ADMIN);
        }

        // 비활성 이력은 이름을 최신화하고 다시 활성 상태로 전환한다.
        existingAdmin.reassign(userCache.getName());
        return existingAdmin;
    }
}
