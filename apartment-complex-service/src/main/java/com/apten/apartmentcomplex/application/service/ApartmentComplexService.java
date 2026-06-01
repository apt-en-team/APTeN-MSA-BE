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

// 단지 관리 응용 서비스
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

    // 단지 등록 요청 검증
    private void validateCreateApartmentComplexReq(ApartmentComplexReq req) {
        if (req == null
                || isBlank(req.getName())
                || isBlank(req.getAddress())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 빈 문자열 확인
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // 다음 단지 코드 생성
    private String generateNextComplexCode() {
        return apartmentComplexRepository.findLastCode()
                .map(code -> increaseComplexCode(code))
                .orElse("APT-0001");
    }

    // 단지 코드 증가
    private String increaseComplexCode(String lastCode) {
        int number = Integer.parseInt(lastCode.substring(4));
        return String.format("APT-%04d", number + 1);
    }

    // 단지 등록
    @Transactional
    public ApartmentComplexPostRes createApartmentComplex(ApartmentComplexReq req) {

        validateCreateApartmentComplexReq(req);

        // 최초 관리자 검증
        validateInitialManager(req);
        // 단지 기능 기본값 보정
        Map<FeatureCode, Boolean> normalizedFeatures = normalizeFeatures(req.getFeatures());

        // 단지명 중복 확인
        if (apartmentComplexRepository.existsByName(req.getName())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX);
        }

        String code = generateNextComplexCode();

        // 단지 원본 저장
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
        // 단지 기능 원본 저장
        saveFeatures(savedApartmentComplex, normalizedFeatures);

        // 최초 관리자 계정 생성 (Auth 내부 API)
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

        // 최초 관리자 단지 소속 저장
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

        // user_cache 비동기 동기화 (Auth 이벤트)
        // TODO: 외부 Auth 호출이 트랜잭션 안에 있으므로 이후 보상 처리 전략을 검토한다.

        // 단지 생성 이벤트 Outbox 적재
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

    // 단지 목록 조회
    @Transactional(readOnly = true)
    public ApartmentComplexGetPageRes getApartmentComplexList(ApartmentComplexSearchReq req) {
        // 검색 조건 변환
        int page = req.getPage();
        int size = req.getSize();
        ApartmentComplexStatus status = parseApartmentComplexStatusOrNull(req.getStatus());

        // 페이지 요청 생성
        PageRequest pageRequest = PageRequest.of(page, size);

        // 상태 필터 분기
        Page<ApartmentComplex> result = status == null
                ? apartmentComplexRepository.findPageByKeyword(req.getKeyword(), pageRequest)
                : apartmentComplexRepository.findPageByKeywordAndStatus(req.getKeyword(), status, pageRequest);
        // 단지 기능 일괄 조회 (N+1 방지)
        Map<Long, Map<String, Boolean>> featureMaps = getFeatureMaps(
                result.getContent().stream().map(ApartmentComplex::getId).toList()
        );

        // 단지 목록 응답 변환
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

        // 페이지 응답 생성
        return ApartmentComplexGetPageRes.builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .build();
    }

    // 단지 상세 조회
    public ApartmentComplexGetDetailRes getApartmentComplexDetail(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));
        return toDetailRes(complex);
    }

    // 단지 수정
    @Transactional
    public ApartmentComplexPatchRes updateApartmentComplex(String code, ApartmentComplexPatchReq req) {
        // 수정 대상 단지 조회
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (req == null || isBlank(req.getName())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 기본 정보 수정 (주소/우편번호 제외)
        complex.updateSummary(req.getName(), req.getDescription());
        // 단지 기능 수정
        upsertFeatures(complex, req.getFeatures());
        // 주차 운영 타입 수정
        if (req.getParkingType() != null) {
            complex.changeParkingType(req.getParkingType());
        }

        // 단지 수정 이벤트 Outbox 적재
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

    // 단지 관리자 지정 (MASTER)
    @Transactional
    public ComplexAdminPostRes assignAdminToComplex(String code, ComplexAdminPostReq req) {
        ApartmentComplex complex = getManagedComplexByCode(code);
        return assignAdminToComplex(complex, req);
    }

    // 내 단지 관리자 지정 (MASTER/MANAGER)
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

    // 단지 관리자 지정 처리
    private ComplexAdminPostRes assignAdminToComplex(ApartmentComplex complex, ComplexAdminPostReq req) {
        // 관리자 생성 요청 검증
        if (req == null
                || isBlank(req.getEmail())
                || isBlank(req.getPassword())
                || isBlank(req.getName())
                || isBlank(req.getPhone())
                || isBlank(req.getAdminRole())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        validateAdminRole(req.getAdminRole());

        // 관리자 계정 생성 (Auth 내부 API)
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

        // 기존 관리자 배정 이력 재사용
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

        // 관리자 단지 소속 저장
        complexAdminRepository.save(admin);

        // user_cache 비동기 동기화 (Auth 이벤트)

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

    // 내 단지 관리자 해제 (MASTER/MANAGER)
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

    // 단지 관리자 해제 처리
    private ComplexAdminDeleteRes unassignAdminFromComplex(ApartmentComplex complex, Long userId) {
        // 관리자 배정 이력 조회
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));

        // 중복 해제 방지
        if (!Boolean.TRUE.equals(admin.getIsActive())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 관리자 소속 비활성화
        admin.unassign();
        complexAdminRepository.save(admin);

        // 관리자 계정 소프트 삭제 (Auth 내부 API)
        InternalAdminDeleteRes deletedAdmin = authInternalClient.softDeleteAdmin(userId);

        // user_cache 비동기 동기화 (Auth 이벤트)
        // TODO: 내부 호출과 DB 상태 변경 사이의 보상 처리 정책을 정리한다.

        return ComplexAdminDeleteRes.builder()
                .code(complex.getCode())
                .userId(admin.getAdminUserId())
                .isActive(admin.getIsActive())
                .unassignedAt(admin.getUnassignedAt())
                .deletedAt(deletedAdmin.getDeletedAt())
                .build();
    }

    // 내 단지 관리자 목록 조회
    @Transactional(readOnly = true)
    public List<ComplexAdminGetRes> getMyComplexAdminList(Long complexId, Long selectedComplexId, String userRole) {
        validateAdminWorkspaceRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return getComplexAdminList(complex);
    }

    // 단지 관리자 목록 변환
    private List<ComplexAdminGetRes> getComplexAdminList(ApartmentComplex complex) {
        // 관리자 현황 응답 변환
        return complexAdminRepository.findByComplexIdOrderByAssignedAtDesc(complex.getId()).stream()
                .map(admin -> ComplexAdminGetRes.builder()
                        .userId(admin.getAdminUserId())
                        .name(admin.getAdminName())
                        .email(admin.getAdminEmail())
                        .phone(admin.getAdminPhone())
                        // 관리자 권한 코드/표시명 응답
                        .adminRole(admin.getAdminRole())
                        .adminRoleName(resolveAdminRoleName(admin.getAdminRole()))
                        .isActive(admin.getIsActive())
                        .assignedAt(admin.getAssignedAt())
                        .unassignedAt(admin.getUnassignedAt())
                        .build())
                .toList();
    }

    // 내 단지 관리자 수정 (MASTER/MANAGER)
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

    // 단지 관리자 수정 처리
    private ComplexAdminPatchRes updateComplexAdmin(ApartmentComplex complex, Long userId, ComplexAdminPatchReq req) {
        // 관리자 배정 이력 조회
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));

        // 관리자 수정 요청 검증
        if (req == null
                || isBlank(req.getName())
                || isBlank(req.getPhone())
                || isBlank(req.getAdminRole())
                || req.getIsActive() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateAdminRole(req.getAdminRole());

        // 관리자 스냅샷 수정
        admin.changeAdminProfile(req.getName(), req.getPhone());
        admin.changeAdminRole(req.getAdminRole());
        admin.changeActive(req.getIsActive());
        complexAdminRepository.save(admin);

        // 관리자 계정 수정 (Auth 내부 API)
        authInternalClient.updateAdmin(
                userId,
                InternalAdminUpdateReq.builder()
                        .name(req.getName())
                        .phone(req.getPhone())
                        .adminRole(req.getAdminRole())
                        .status(Boolean.TRUE.equals(req.getIsActive()) ? "01" : "02")
                        .build()
        );

        // user_cache 비동기 동기화 (Auth 이벤트)
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

    // 관리자 내 단지 조회
    @Transactional(readOnly = true)
    public ApartmentComplexGetDetailRes getMyApartmentComplex(Long complexId, Long selectedComplexId, String userRole) {
        validateAdminWorkspaceRole(userRole);
        ApartmentComplex complex = getManagedComplexForAdminContext(userRole, complexId, selectedComplexId);
        return toDetailRes(complex);
    }

    // 입주민 내 단지 조회
    @Transactional(readOnly = true)
    public ApartmentComplexGetDetailRes getMyApartmentComplexForResident(String userRole, Long complexId) {
        validateResidentWorkspaceRole(userRole);
        ApartmentComplex complex = getManagedComplexById(complexId);
        return toDetailRes(complex);
    }

    // 단지 상태 변경
    @Transactional
    public ApartmentComplexStatusPatchRes changeApartmentComplexStatus(String code, ApartmentComplexStatusPatchReq req) {
        // 상태 변경 대상 조회
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 상태 변경 요청 검증
        if (req == null || isBlank(req.getStatus())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 상태 변환
        ApartmentComplexStatus status = parseApartmentComplexStatus(req.getStatus());

        // 단지 상태 반영
        complex.changeStatus(status);

        // 상태별 Outbox 이벤트 분기
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

    // MASTER 단지 선택
    @Transactional(readOnly = true)
    public ApartmentComplexSelectRes selectApartmentComplex(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (complex.getStatus() == ApartmentComplexStatus.DELETED) {
            throw new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND);
        }

        // 관리자 화면 진입 경로 설정
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

    // 공개 단지 목록 조회 (활성 단지)
    @Transactional(readOnly = true)
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // 활성 단지 목록 조회
        return apartmentComplexRepository.findPublicListByKeyword(keyword, ApartmentComplexStatus.ACTIVE)
                .stream()
                .map(complex -> ApartmentComplexPublicRes.builder()
                        // 가입용 단지 식별값 응답
                        .complexId(complex.getId())
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .build())
                .toList();
    }

    // 최초 관리자 요청 검증
    private void validateInitialManager(ApartmentComplexReq req) {
        if (isBlank(req.getManagerEmail())
                || isBlank(req.getManagerPassword())
                || isBlank(req.getManagerName())
                || isBlank(req.getManagerPhone())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 관리자 권한 검증
    private void validateAdminRole(String adminRole) {
        if (!"01".equals(adminRole) && !"02".equals(adminRole)) {
            throw new BusinessException(ApartmentComplexErrorCode.INVALID_ADMIN_ROLE);
        }
    }

    // 단지 기능 기본값 생성 (전체 활성)
    private Map<String, Boolean> getDefaultFeatureMap() {
        LinkedHashMap<String, Boolean> defaults = new LinkedHashMap<>();
        for (FeatureCode featureCode : FeatureCode.values()) {
            defaults.put(featureCode.name(), true);
        }
        return defaults;
    }

    // 단지 등록 기능 설정 보정 (누락값 활성)
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

    // 단지 수정 기능 설정 보정 (요청값만 반영)
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

    // 단지 기능 코드 변환
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

    // 단지 기능 원본 저장
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

    // 단지 기능 저장/수정 (요청값만 반영)
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

    // 단일 단지 기능 조회 (누락값 활성)
    private Map<String, Boolean> getFeatureMap(Long complexId) {
        LinkedHashMap<String, Boolean> featureMap = new LinkedHashMap<>(getDefaultFeatureMap());
        complexFeatureRepository.findByComplex_Id(complexId)
                .forEach(feature -> featureMap.put(feature.getFeatureCode().name(), feature.isEnabled()));
        return featureMap;
    }

    // 복수 단지 기능 조회 (N+1 방지)
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

    // 단지 상세 응답 변환
    private ApartmentComplexGetDetailRes toDetailRes(ApartmentComplex complex) {
        return ApartmentComplexGetDetailRes.builder()
                .complexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .address(complex.getAddress())
                // 우편번호 응답 필드 통일
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

    // 단지 기능 응답 변환
    private Map<String, Boolean> toFeatureResponseMap(Map<FeatureCode, Boolean> features) {
        LinkedHashMap<String, Boolean> response = new LinkedHashMap<>();
        for (FeatureCode featureCode : FeatureCode.values()) {
            response.put(featureCode.name(), features.getOrDefault(featureCode, true));
        }
        return response;
    }

    // 관리자 화면 접근 권한 검증
    private void validateAdminWorkspaceRole(String userRole) {
        if (!"MASTER".equalsIgnoreCase(userRole)
                && !"MANAGER".equalsIgnoreCase(userRole)
                && !"ADMIN".equalsIgnoreCase(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    // 관리자 변경 권한 검증
    private void validateManagerOrMasterRole(String userRole) {
        if (!"MASTER".equalsIgnoreCase(userRole) && !"MANAGER".equalsIgnoreCase(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    // 입주민 화면 접근 권한 검증
    private void validateResidentWorkspaceRole(String userRole) {
        if (isBlank(userRole)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (!"USER".equalsIgnoreCase(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    // 관리 대상 단지 조회 (단지 코드)
    private ApartmentComplex getManagedComplexByCode(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (complex.getStatus() == ApartmentComplexStatus.DELETED) {
            throw new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND);
        }

        return complex;
    }

    // 관리 대상 단지 조회 (단지 ID)
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

    // 관리자 컨텍스트 단지 조회
    private ApartmentComplex getManagedComplexForAdminContext(String userRole, Long complexId, Long selectedComplexId) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        return getManagedComplexById(targetComplexId);
    }

    // 관리자 컨텍스트 단지 ID 결정 (MASTER 선택 단지, ADMIN/MANAGER 소속 단지)
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

    // 단지 상태 변환 (코드/enum/표시명)
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

    // 선택 단지 상태 변환
    private ApartmentComplexStatus parseApartmentComplexStatusOrNull(String rawStatus) {
        if (isBlank(rawStatus)) {
            return null;
        }
        return parseApartmentComplexStatus(rawStatus);
    }

    // 단지 상태 코드 변환
    private String toStatusCode(ApartmentComplexStatus status) {
        return status.getCode();
    }

    // 단지 상태 표시명 변환
    private String toStatusName(ApartmentComplexStatus status) {
        return status.getValue();
    }

    // 주차 운영 타입 코드 변환
    private String toParkingTypeCode(ParkingType parkingType) {
        return parkingType == null ? null : parkingType.getCode();
    }

    // 주차 운영 타입 표시명 변환
    private String toParkingTypeValue(ParkingType parkingType) {
        return parkingType == null ? null : parkingType.getValue();
    }

    // 관리자 권한 표시명 변환
    private String resolveAdminRoleName(String adminRole) {
        if ("01".equals(adminRole) || "MANAGER".equalsIgnoreCase(adminRole)) {
            return "매니저";
        }
        if ("02".equals(adminRole) || "ADMIN".equalsIgnoreCase(adminRole)) {
            return "어드민";
        }
        return "";
    }

    // 관리자 소속 재활성화
    private ComplexAdmin reactivateAdminAssignment(
            ComplexAdmin existingAdmin,
            InternalAdminCreateRes createdAdmin,
            ComplexAdminPostReq req
    ) {
        // 활성 소속 중복 방지
        if (Boolean.TRUE.equals(existingAdmin.getIsActive())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX_ADMIN);
        }

        // 비활성 소속 재사용
        existingAdmin.reassign(
                createdAdmin.getName(),
                createdAdmin.getEmail(),
                defaultIfBlank(createdAdmin.getPhone(), req.getPhone()),
                req.getAdminRole()
        );
        return existingAdmin;
    }

    // 빈 문자열 기본값 적용
    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }
}
