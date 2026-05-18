package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexPatchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexStatusPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexSelectRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexStatusPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminDeleteRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminGetRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.entity.ComplexFeature;
import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexFeatureRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexAdminRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.AuthInternalClient;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateRes;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminDeleteRes;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminUpdateReq;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexMapper;
import com.apten.common.enums.FeatureCode;
import com.apten.common.enums.ParkingType;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final ComplexFeatureRepository complexFeatureRepository;
    private final ComplexAdminRepository complexAdminRepository;
    private final AuthInternalClient authInternalClient;

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

        // 단지 등록 요청에 포함된 최초 관리자 정보를 검증한다.
        validateInitialManager(req);
        // 단지 기능 설정은 등록 전에 기본값과 허용 코드 기준으로 정규화한다.
        Map<FeatureCode, Boolean> normalizedFeatures = normalizeFeatures(req.getFeatures());

        //중복 체크
        if (apartmentComplexRepository.existsByName(req.getName())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX);
        }

        String code = generateNextComplexCode();

        // 단지 정보를 먼저 저장해 complexId를 확보한다.
        ApartmentComplex apartmentComplex = ApartmentComplex.builder()
                .code(code)
                .name(req.getName())
                .address(req.getAddress())
                .zipCode(req.getZipCode())
                .status(ApartmentComplexStatus.ACTIVE)
                .description(req.getDescription())
                .parkingType(req.getParkingType() != null ? req.getParkingType() : ParkingType.NONE)
                .build();
        ApartmentComplex savedApartmentComplex = apartmentComplexRepository.save(apartmentComplex);
        // 단지 저장 직후 complex_feature 원본을 함께 저장한다.
        saveFeatures(savedApartmentComplex, normalizedFeatures);

        // Auth Service 내부 API를 호출해 최초 관리자 계정을 생성한다.
        InternalAdminCreateRes createdAdmin = authInternalClient.createAdmin(
                InternalAdminCreateReq.builder()
                        .complexId(savedApartmentComplex.getId())
                        .email(req.getManagerEmail())
                        .password(req.getManagerPassword())
                        .name(req.getManagerName())
                        .phone(req.getManagerPhone())
                        .adminRole("01")
                        .build()
        );

        // Auth 응답의 userId를 기준으로 단지 관리자 소속을 저장한다.
        ComplexAdmin complexAdmin = ComplexAdmin.builder()
                .complexId(savedApartmentComplex.getId())
                .adminUserId(createdAdmin.getUserId())
                .adminName(createdAdmin.getName())
                .adminEmail(createdAdmin.getEmail())
                .adminPhone(defaultIfBlank(createdAdmin.getPhone(), req.getManagerPhone()))
                .adminRole("01")
                .isActive(true)
                .assignedAt(LocalDateTime.now())
                .build();
        complexAdminRepository.save(complexAdmin);

        // user_cache는 Auth 이벤트를 통해 비동기로 동기화되므로 여기서 직접 저장하지 않는다.
        // Kafka Outbox 구조는 기존 단지 이벤트 발행 흐름을 그대로 유지한다.
        // TODO: 외부 Auth 호출이 트랜잭션 안에 있으므로 이후 보상 처리 전략을 검토한다.

        // Kafka 전송은 relay가 담당하므로 같은 트랜잭션 안에서는 Outbox row만 남긴다
        apartmentComplexOutboxService.saveCreatedEvent(savedApartmentComplex);

        return ApartmentComplexPostRes.builder()
                .complexId(savedApartmentComplex.getId())
                .code(savedApartmentComplex.getCode())
                .name(savedApartmentComplex.getName())
                .managerUserId(createdAdmin.getUserId())
                .managerName(createdAdmin.getName())
                .managerEmail(createdAdmin.getEmail())
                .managerPhone(defaultIfBlank(createdAdmin.getPhone(), req.getManagerPhone()))
                .features(toFeatureResponseMap(normalizedFeatures))
                .parkingTypeCode(toParkingTypeCode(savedApartmentComplex.getParkingType()))
                .parkingTypeValue(toParkingTypeValue(savedApartmentComplex.getParkingType()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 단지 목록 조회 서비스 API-202
    @Transactional(readOnly = true)
    public ApartmentComplexGetPageRes getApartmentComplexList(ApartmentComplexSearchReq req) {
        // 요청값이 없을 때 기본 페이지와 사이즈를 설정한다.
        int page = req.getPage();
        int size = req.getSize();
        ApartmentComplexStatus status = parseApartmentComplexStatusOrNull(req.getStatus());

        // JPA 페이징 처리를 위한 PageRequest를 생성한다.
        PageRequest pageRequest = PageRequest.of(page, size);

        // status가 없으면 전체 상태를, 있으면 선택 상태만 조회한다.
        Page<ApartmentComplex> result = status == null
                ? apartmentComplexRepository.findPageByKeyword(req.getKeyword(), pageRequest)
                : apartmentComplexRepository.findPageByKeywordAndStatus(req.getKeyword(), status, pageRequest);
        // 목록 응답의 기능 정보는 현재 페이지 단지들만 일괄 조회해 N+1을 줄인다.
        Map<Long, Map<String, Boolean>> featureMaps = getFeatureMaps(
                result.getContent().stream().map(ApartmentComplex::getId).toList()
        );

        // 조회된 엔티티 목록을 API 응답 DTO 목록으로 변환한다.
        List<ApartmentComplexGetRes> content = result.getContent()
                .stream()
                .map(complex -> ApartmentComplexGetRes.builder()
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .status(toStatusCode(complex.getStatus()))
                        .statusName(toStatusName(complex.getStatus()))
                        .description(complex.getDescription())
                        .features(featureMaps.getOrDefault(complex.getId(), getDefaultFeatureMap()))
                        .parkingTypeCode(toParkingTypeCode(complex.getParkingType()))
                        .parkingTypeValue(toParkingTypeValue(complex.getParkingType()))
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
                .complexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .address(complex.getAddress())
                // 외부 응답 필드명은 zipCode로 통일한다.
                .zipCode(complex.getZipCode())
                .status(toStatusCode(complex.getStatus()))
                .statusName(toStatusName(complex.getStatus()))
                .description(complex.getDescription())
                .features(getFeatureMap(complex.getId()))
                .parkingTypeCode(toParkingTypeCode(complex.getParkingType()))
                .parkingTypeValue(toParkingTypeValue(complex.getParkingType()))
                .createdAt(complex.getCreatedAt())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 단지 수정 서비스 API-204
    @Transactional
    public ApartmentComplexPatchRes updateApartmentComplex(String code, ApartmentComplexPatchReq req) {
        // API의 단지 code로 보고 수정 대상 단지를 조회한다
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (req == null || isBlank(req.getName())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 수정에서는 주소와 우편번호를 보존하고 이름과 설명만 변경한다.
        complex.updateSummary(req.getName(), req.getDescription());
        // 기능 설정 요청이 있으면 단지 기능 사용 여부도 함께 갱신한다.
        upsertFeatures(complex, req.getFeatures());
        // 주차 운영 타입 요청이 있으면 단지 원본의 parking_type을 함께 갱신한다.
        if (req.getParkingType() != null) {
            complex.changeParkingType(req.getParkingType());
        }

        // Kafka 직접 발행 대신 수정 이벤트를 같은 트랜잭션 안에서 Outbox에 적재한다
        apartmentComplexOutboxService.saveUpdatedEvent(complex);

        return ApartmentComplexPatchRes.builder()
                .code(code)
                .name(complex.getName())
                .description(complex.getDescription())
                .parkingTypeCode(toParkingTypeCode(complex.getParkingType()))
                .parkingTypeValue(toParkingTypeValue(complex.getParkingType()))
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 관리자 단지 소속 지정 서비스 API-206이다.
    @Transactional
    public ComplexAdminPostRes assignAdminToComplex(String code, ComplexAdminPostReq req) {
        ApartmentComplex complex = getManagedComplexByCode(code);
        return assignAdminToComplex(complex, req);
    }

    // 일반 관리자는 헤더의 complexId 기준으로 자기 단지에만 접근한다.
    @Transactional
    public ComplexAdminPostRes assignAdminToMyComplex(
            Long complexId,
            Long selectedComplexId,
            String userRole,
            ComplexAdminPostReq req
    ) {
        validateManagerOrMasterRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return assignAdminToComplex(complex, req);
    }

    private ComplexAdminPostRes assignAdminToComplex(ApartmentComplex complex, ComplexAdminPostReq req) {
        // 관리자 생성 요청의 필수값과 역할 코드를 검증한다.
        if (req == null
                || isBlank(req.getEmail())
                || isBlank(req.getPassword())
                || isBlank(req.getName())
                || isBlank(req.getPhone())
                || isBlank(req.getAdminRole())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        validateAdminRole(req.getAdminRole());

        // 관리자 생성 시 Auth Service 내부 API 호출 예정이다.
        InternalAdminCreateRes createdAdmin = authInternalClient.createAdmin(
                InternalAdminCreateReq.builder()
                        .complexId(complex.getId())
                        .email(req.getEmail())
                        .password(req.getPassword())
                        .name(req.getName())
                        .phone(req.getPhone())
                        .adminRole(req.getAdminRole())
                        .build()
        );

        // 같은 단지에 대한 기존 배정 이력이 있으면 활성 상태에 따라 재사용한다.
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), createdAdmin.getUserId())
                .map(existingAdmin -> reactivateAdminAssignment(existingAdmin, createdAdmin, req))
                .orElseGet(() -> ComplexAdmin.builder()
                        .complexId(complex.getId())
                        .adminUserId(createdAdmin.getUserId())
                        .adminName(createdAdmin.getName())
                        .adminEmail(createdAdmin.getEmail())
                        .adminPhone(defaultIfBlank(createdAdmin.getPhone(), req.getPhone()))
                        .adminRole(req.getAdminRole())
                        .isActive(true)
                        .assignedAt(LocalDateTime.now())
                        .build());

        // Auth 응답의 userId를 기준으로 단지 관리자 소속을 저장한다.
        complexAdminRepository.save(admin);

        // user_cache는 Auth 이벤트를 통해 비동기로 동기화되므로 여기서 직접 저장하지 않는다.

        return ComplexAdminPostRes.builder()
                .code(complex.getCode())
                .userId(admin.getAdminUserId())
                .name(admin.getAdminName())
                .email(admin.getAdminEmail())
                .phone(admin.getAdminPhone())
                .adminRole(admin.getAdminRole())
                .adminRoleName(resolveAdminRoleName(admin.getAdminRole()))
                .isActive(admin.getIsActive())
                .assignedAt(admin.getAssignedAt())
                .build();
    }

    // 일반 관리자는 자기 단지 관리자 소속만 해제할 수 있다.
    @Transactional
    public ComplexAdminDeleteRes unassignAdminFromMyComplex(
            Long complexId,
            Long selectedComplexId,
            String userRole,
            Long userId
    ) {
        validateManagerOrMasterRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return unassignAdminFromComplex(complex, userId);
    }

    private ComplexAdminDeleteRes unassignAdminFromComplex(ApartmentComplex complex, Long userId) {
        // complex_admin에서 현재 또는 과거 배정 이력을 조회한다.
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));

        // 이미 비활성 상태면 다시 해제할 수 없으므로 잘못된 요청으로 처리한다.
        if (!Boolean.TRUE.equals(admin.getIsActive())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 관리자 소속은 실제 삭제하지 않고 비활성 처리한다.
        admin.unassign();
        complexAdminRepository.save(admin);

        // Auth Service 내부 API를 호출해 관리자 계정을 소프트 삭제한다.
        InternalAdminDeleteRes deletedAdmin = authInternalClient.softDeleteAdmin(userId);

        // user_cache는 Auth 이벤트로 비동기 동기화되므로 여기서 직접 수정하지 않는다.
        // Kafka Outbox 구조는 기존 이벤트 발행 흐름을 그대로 유지한다.
        // TODO: 내부 호출과 DB 상태 변경 사이의 보상 처리 정책을 정리한다.

        return ComplexAdminDeleteRes.builder()
                .code(complex.getCode())
                .userId(admin.getAdminUserId())
                .isActive(admin.getIsActive())
                .unassignedAt(admin.getUnassignedAt())
                .deletedAt(deletedAdmin.getDeletedAt())
                .build();
    }

    // 일반 관리자는 자기 단지 관리자 목록만 조회할 수 있다.
    @Transactional(readOnly = true)
    public List<ComplexAdminGetRes> getMyComplexAdminList(Long complexId, Long selectedComplexId, String userRole) {
        validateAdminWorkspaceRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return getComplexAdminList(complex);
    }

    private List<ComplexAdminGetRes> getComplexAdminList(ApartmentComplex complex) {
        // 프론트에서 현재 상태를 함께 보여줄 수 있도록 전체 관리자 현황을 내려준다.
        return complexAdminRepository.findByComplexIdOrderByAssignedAtDesc(complex.getId()).stream()
                .map(admin -> ComplexAdminGetRes.builder()
                        .userId(admin.getAdminUserId())
                        .name(admin.getAdminName())
                        .email(admin.getAdminEmail())
                        .phone(admin.getAdminPhone())
                        // 프론트 수정 모달에서 사용할 수 있도록 권한 code와 표시명을 함께 내려준다.
                        .adminRole(admin.getAdminRole())
                        .adminRoleName(resolveAdminRoleName(admin.getAdminRole()))
                        .isActive(admin.getIsActive())
                        .assignedAt(admin.getAssignedAt())
                        .unassignedAt(admin.getUnassignedAt())
                        .build())
                .toList();
    }

    // 일반 관리자는 자기 단지 관리자 정보만 수정할 수 있다.
    @Transactional
    public ComplexAdminPatchRes updateMyComplexAdmin(
            Long complexId,
            Long selectedComplexId,
            String userRole,
            Long userId,
            ComplexAdminPatchReq req
    ) {
        validateManagerOrMasterRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return updateComplexAdmin(complex, userId, req);
    }

    private ComplexAdminPatchRes updateComplexAdmin(ApartmentComplex complex, Long userId, ComplexAdminPatchReq req) {
        // complex_admin 조회
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));

        // 관리자 수정 시 adminRole/isActive 검증
        if (req == null
                || isBlank(req.getName())
                || isBlank(req.getPhone())
                || isBlank(req.getAdminRole())
                || req.getIsActive() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateAdminRole(req.getAdminRole());

        // 관리자 수정에서는 이름과 연락처 스냅샷을 함께 갱신한다.
        admin.changeAdminProfile(req.getName(), req.getPhone());
        admin.changeAdminRole(req.getAdminRole());
        admin.changeActive(req.getIsActive());
        complexAdminRepository.save(admin);

        // 관리자 수정 시 Auth Service와 local 스냅샷을 함께 갱신한다.
        authInternalClient.updateAdmin(
                userId,
                InternalAdminUpdateReq.builder()
                        .name(req.getName())
                        .phone(req.getPhone())
                        .adminRole(req.getAdminRole())
                        .status(Boolean.TRUE.equals(req.getIsActive()) ? "01" : "02")
                        .build()
        );

        // user_cache는 Auth 이벤트로 비동기 동기화되므로 여기서 직접 수정하지 않는다.
        // Kafka Outbox 구조는 기존 이벤트 발행 흐름을 그대로 유지한다.
        // TODO: 내부 호출과 DB 상태 변경 사이의 보상 처리 정책을 정리한다.

        return ComplexAdminPatchRes.builder()
                .userId(admin.getAdminUserId())
                .name(admin.getAdminName())
                .email(admin.getAdminEmail())
                .phone(admin.getAdminPhone())
                .adminRole(admin.getAdminRole())
                .adminRoleName(resolveAdminRoleName(admin.getAdminRole()))
                .isActive(admin.getIsActive())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }

    // 일반 관리자는 자기 단지 기본 정보만 code 없이 조회한다.
    @Transactional(readOnly = true)
    public ApartmentComplexGetDetailRes getMyApartmentComplex(Long complexId, Long selectedComplexId, String userRole) {
        validateAdminWorkspaceRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return ApartmentComplexGetDetailRes.builder()
                .complexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .address(complex.getAddress())
                // 외부 응답 필드명은 zipCode로 통일한다.
                .zipCode(complex.getZipCode())
                .status(toStatusCode(complex.getStatus()))
                .statusName(toStatusName(complex.getStatus()))
                .description(complex.getDescription())
                .features(getFeatureMap(complex.getId()))
                .parkingTypeCode(toParkingTypeCode(complex.getParkingType()))
                .parkingTypeValue(toParkingTypeValue(complex.getParkingType()))
                .createdAt(complex.getCreatedAt())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 입주민은 토큰 단지 헤더 기준으로 자기 단지 기본 정보만 조회한다.
    @Transactional(readOnly = true)
    public ApartmentComplexGetDetailRes getMyApartmentComplexForResident(String userRole, Long complexId) {
        validateResidentWorkspaceRole(userRole);
        ApartmentComplex complex = getManagedComplexById(complexId);
        return ApartmentComplexGetDetailRes.builder()
                .complexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .address(complex.getAddress())
                .zipCode(complex.getZipCode())
                .status(toStatusCode(complex.getStatus()))
                .statusName(toStatusName(complex.getStatus()))
                .description(complex.getDescription())
                .features(getFeatureMap(complex.getId()))
                .parkingTypeCode(toParkingTypeCode(complex.getParkingType()))
                .parkingTypeValue(toParkingTypeValue(complex.getParkingType()))
                .createdAt(complex.getCreatedAt())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 단지 활성 상태를 별도 API에서 변경한다.
    @Transactional
    public ApartmentComplexStatusPatchRes changeApartmentComplexStatus(String code, ApartmentComplexStatusPatchReq req) {
        // 단지 코드로 상태 변경 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 바꿀 상태가 없으면 요청이 잘못된 것으로 본다.
        if (req == null || isBlank(req.getStatus())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 상태 문자열은 code, enum name, 한글 표시명을 모두 허용한다.
        ApartmentComplexStatus status = parseApartmentComplexStatus(req.getStatus());

        // 단지 상태를 별도 API에서만 바꾸도록 명시적으로 반영한다.
        complex.changeStatus(status);

        // 단지 비활성화는 별도 이벤트로, 나머지 상태 변경은 일반 수정 이벤트로 적재한다.
        if (status == ApartmentComplexStatus.INACTIVE) {
            apartmentComplexOutboxService.saveDeactivatedEvent(complex);
        } else {
            apartmentComplexOutboxService.saveUpdatedEvent(complex);
        }

        return ApartmentComplexStatusPatchRes.builder()
                .code(complex.getCode())
                .status(toStatusCode(complex.getStatus()))
                .statusName(toStatusName(complex.getStatus()))
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 마스터 단지 선택 화면에는 complexId와 상태, 관리자 화면 URL을 함께 내려준다.
    @Transactional(readOnly = true)
    public ApartmentComplexSelectRes selectApartmentComplex(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (complex.getStatus() == ApartmentComplexStatus.DELETED) {
            throw new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND);
        }

        // 선택 단지 정보는 공통 관리자 화면으로 진입할 수 있게 내려준다.
        String adminPageUrl = "/admin/dashboard";

        return ApartmentComplexSelectRes.builder()
                .complexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .status(toStatusCode(complex.getStatus()))
                .statusName(toStatusName(complex.getStatus()))
                .features(getFeatureMap(complex.getId()))
                .parkingTypeCode(toParkingTypeCode(complex.getParkingType()))
                .parkingTypeValue(toParkingTypeValue(complex.getParkingType()))
                .adminPageUrl(adminPageUrl)
                .build();
    }

    // 공개 단지 목록 조회 서비스 API-209이다.
    @Transactional(readOnly = true)
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // 회원가입과 단지 선택 화면에는 활성 단지만 노출한다.
        return apartmentComplexRepository.findPublicListByKeyword(keyword, ApartmentComplexStatus.ACTIVE)
                .stream()
                .map(complex -> ApartmentComplexPublicRes.builder()
                        // 회원가입 화면에서는 code와 함께 complexId도 내려준다.
                        .complexId(complex.getId())
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .build())
                .toList();
    }

    private void validateInitialManager(ApartmentComplexReq req) {
        if (isBlank(req.getManagerEmail())
                || isBlank(req.getManagerPassword())
                || isBlank(req.getManagerName())
                || isBlank(req.getManagerPhone())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateAdminRole(String adminRole) {
        if (!"01".equals(adminRole) && !"02".equals(adminRole)) {
            throw new BusinessException(ApartmentComplexErrorCode.INVALID_ADMIN_ROLE);
        }
    }

    // 단지 기능 기본값은 모든 기능을 사용으로 둔다.
    private Map<String, Boolean> getDefaultFeatureMap() {
        LinkedHashMap<String, Boolean> defaults = new LinkedHashMap<>();
        for (FeatureCode featureCode : FeatureCode.values()) {
            defaults.put(featureCode.name(), true);
        }
        return defaults;
    }

    // 단지 등록 기능 요청은 허용 코드와 null 여부를 검증하고 누락값을 true로 보정한다.
    private Map<FeatureCode, Boolean> normalizeFeatures(Map<String, Boolean> requestFeatures) {
        LinkedHashMap<FeatureCode, Boolean> normalized = new LinkedHashMap<>();
        for (FeatureCode featureCode : FeatureCode.values()) {
            normalized.put(featureCode, true);
        }

        if (requestFeatures == null) {
            return normalized;
        }

        for (Map.Entry<String, Boolean> entry : requestFeatures.entrySet()) {
            FeatureCode featureCode = parseFeatureCode(entry.getKey());
            if (entry.getValue() == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            normalized.put(featureCode, entry.getValue());
        }

        return normalized;
    }

    // 단지 수정 기능 요청은 넘어온 키만 검증하고 기존값에 덮어쓸 준비를 한다.
    private Map<FeatureCode, Boolean> normalizeFeatureUpdates(Map<String, Boolean> requestFeatures) {
        if (requestFeatures == null) {
            return Map.of();
        }

        LinkedHashMap<FeatureCode, Boolean> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : requestFeatures.entrySet()) {
            FeatureCode featureCode = parseFeatureCode(entry.getKey());
            if (entry.getValue() == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            normalized.put(featureCode, entry.getValue());
        }
        return normalized;
    }

    // 문자열 기능 코드는 enum name 기준으로 검증하고 통일한다.
    private FeatureCode parseFeatureCode(String rawFeatureCode) {
        if (isBlank(rawFeatureCode)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        try {
            return FeatureCode.valueOf(rawFeatureCode.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 정규화된 기능 설정을 complex_feature 원본 테이블에 저장한다.
    private void saveFeatures(ApartmentComplex complex, Map<FeatureCode, Boolean> features) {
        List<ComplexFeature> featureEntities = features.entrySet().stream()
                .map(entry -> ComplexFeature.builder()
                        .complex(complex)
                        .featureCode(entry.getKey())
                        .enabled(entry.getValue())
                        .build())
                .toList();
        complexFeatureRepository.saveAll(featureEntities);
    }

    // 단지 수정에서는 전달된 기능만 변경하고 누락된 기능은 기존값을 유지한다.
    private void upsertFeatures(ApartmentComplex complex, Map<String, Boolean> requestFeatures) {
        if (requestFeatures == null) {
            return;
        }

        Map<FeatureCode, Boolean> updates = normalizeFeatureUpdates(requestFeatures);
        List<ComplexFeature> existingFeatures = complexFeatureRepository.findByComplex_Id(complex.getId());
        Map<FeatureCode, ComplexFeature> existingFeatureMap = existingFeatures.stream()
                .collect(Collectors.toMap(ComplexFeature::getFeatureCode, Function.identity()));

        for (Map.Entry<FeatureCode, Boolean> entry : updates.entrySet()) {
            ComplexFeature existingFeature = existingFeatureMap.get(entry.getKey());
            if (existingFeature != null) {
                existingFeature.updateEnabled(entry.getValue());
                continue;
            }

            complexFeatureRepository.save(
                    ComplexFeature.builder()
                            .complex(complex)
                            .featureCode(entry.getKey())
                            .enabled(entry.getValue())
                            .build()
            );
        }
    }

    // 단일 단지 기능 응답은 누락된 row가 있어도 모든 기능 키가 내려가게 보정한다.
    private Map<String, Boolean> getFeatureMap(Long complexId) {
        LinkedHashMap<String, Boolean> featureMap = new LinkedHashMap<>(getDefaultFeatureMap());
        complexFeatureRepository.findByComplex_Id(complexId)
                .forEach(feature -> featureMap.put(feature.getFeatureCode().name(), feature.isEnabled()));
        return featureMap;
    }

    // 목록 응답에서는 현재 페이지 단지들만 한 번에 조회해 feature map을 만든다.
    private Map<Long, Map<String, Boolean>> getFeatureMaps(List<Long> complexIds) {
        if (complexIds == null || complexIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, LinkedHashMap<String, Boolean>> featureMaps = complexIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        ignored -> new LinkedHashMap<>(getDefaultFeatureMap()),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        complexFeatureRepository.findByComplex_IdIn(complexIds).forEach(feature -> {
            LinkedHashMap<String, Boolean> featureMap = featureMaps.get(feature.getComplex().getId());
            if (featureMap != null) {
                featureMap.put(feature.getFeatureCode().name(), feature.isEnabled());
            }
        });

        return featureMaps.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Map.copyOf(entry.getValue())));
    }

    // 저장 직후 기능 응답은 enum key를 문자열 key로 변환해 내려준다.
    private Map<String, Boolean> toFeatureResponseMap(Map<FeatureCode, Boolean> features) {
        LinkedHashMap<String, Boolean> response = new LinkedHashMap<>();
        for (FeatureCode featureCode : FeatureCode.values()) {
            response.put(featureCode.name(), features.getOrDefault(featureCode, true));
        }
        return response;
    }

    private void validateAdminWorkspaceRole(String userRole) {
        if (!"MASTER".equalsIgnoreCase(userRole)
                && !"MANAGER".equalsIgnoreCase(userRole)
                && !"ADMIN".equalsIgnoreCase(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    private void validateManagerOrMasterRole(String userRole) {
        if (!"MASTER".equalsIgnoreCase(userRole) && !"MANAGER".equalsIgnoreCase(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    private void validateResidentWorkspaceRole(String userRole) {
        if (isBlank(userRole)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (!"USER".equalsIgnoreCase(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    private ApartmentComplex getManagedComplexByCode(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (complex.getStatus() == ApartmentComplexStatus.DELETED) {
            throw new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND);
        }

        return complex;
    }

    private ApartmentComplex getManagedComplexById(Long complexId) {
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        ApartmentComplex complex = apartmentComplexRepository.findById(complexId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (complex.getStatus() == ApartmentComplexStatus.DELETED) {
            throw new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND);
        }

        return complex;
    }

    // 관리자 업무용 단지 컨텍스트는 MASTER와 일반 관리자의 헤더 규칙으로 결정한다.
    private ApartmentComplex getManagedComplexForAdminContext(String userRole, Long complexId, Long selectedComplexId) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        return getManagedComplexById(targetComplexId);
    }

    // MASTER는 선택 단지 헤더를, MANAGER/ADMIN은 토큰 단지 헤더를 사용한다.
    private Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if ("MASTER".equalsIgnoreCase(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }

    // 상태 요청은 code, enum name, 한글 표시명까지 허용한다.
    private ApartmentComplexStatus parseApartmentComplexStatus(String rawStatus) {
        if (isBlank(rawStatus)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        String normalized = rawStatus.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "01", "ACTIVE", "활성" -> ApartmentComplexStatus.ACTIVE;
            case "02", "INACTIVE", "비활성" -> ApartmentComplexStatus.INACTIVE;
            case "03", "DELETED", "삭제" -> ApartmentComplexStatus.DELETED;
            default -> throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        };
    }

    private ApartmentComplexStatus parseApartmentComplexStatusOrNull(String rawStatus) {
        if (isBlank(rawStatus)) {
            return null;
        }
        return parseApartmentComplexStatus(rawStatus);
    }

    // enum 상태는 API 응답에서 code로 통일한다.
    private String toStatusCode(ApartmentComplexStatus status) {
        return status.getCode();
    }

    // enum 상태는 API 응답에서 표시명도 함께 내려줄 수 있게 변환한다.
    private String toStatusName(ApartmentComplexStatus status) {
        return status.getValue();
    }

    // 주차 운영 타입 code 변환 — null 방어 포함
    private String toParkingTypeCode(ParkingType parkingType) {
        return parkingType == null ? null : parkingType.getCode();
    }

    // 주차 운영 타입 표시 value 변환 — null 방어 포함
    private String toParkingTypeValue(ParkingType parkingType) {
        return parkingType == null ? null : parkingType.getValue();
    }

    private String resolveAdminRoleName(String adminRole) {
        if ("01".equals(adminRole) || "MANAGER".equalsIgnoreCase(adminRole)) {
            return "매니저";
        }
        if ("02".equals(adminRole) || "ADMIN".equalsIgnoreCase(adminRole)) {
            return "어드민";
        }
        return "";
    }

    // 기존 비활성 배정은 재활성화하고, 이미 활성 배정이면 중복으로 본다.
    private ComplexAdmin reactivateAdminAssignment(
            ComplexAdmin existingAdmin,
            InternalAdminCreateRes createdAdmin,
            ComplexAdminPostReq req
    ) {
        // 이미 활성화된 배정이면 같은 단지에 다시 배정할 수 없다.
        if (Boolean.TRUE.equals(existingAdmin.getIsActive())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX_ADMIN);
        }

        // 비활성 이력은 이름을 최신화하고 다시 활성 상태로 전환한다.
        existingAdmin.reassign(
                createdAdmin.getName(),
                createdAdmin.getEmail(),
                defaultIfBlank(createdAdmin.getPhone(), req.getPhone()),
                req.getAdminRole()
        );
        return existingAdmin;
    }

    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }
}
