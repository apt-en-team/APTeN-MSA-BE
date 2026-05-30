package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.AdminVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.VehicleListReq;
import com.apten.parkingvehicle.application.model.request.VehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VehicleRejectReq;
import com.apten.parkingvehicle.application.model.response.AdminVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.AdminVehicleListRes;
import com.apten.parkingvehicle.application.model.response.LicensePlateCheckRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VehicleApproveRes;
import com.apten.parkingvehicle.application.model.response.VehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.VehicleListRes;
import com.apten.parkingvehicle.application.model.response.VehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VehicleRejectRes;
import com.apten.parkingvehicle.application.support.RoleContextValidator;
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.HouseholdMemberCache;
import com.apten.parkingvehicle.domain.entity.ParkingLog;
import com.apten.parkingvehicle.domain.entity.UserCache;
import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.HouseholdMemberCacheRepository;
import com.apten.parkingvehicle.domain.repository.ParkingLogRepository;
import com.apten.parkingvehicle.domain.repository.UserCacheRepository;
import com.apten.parkingvehicle.domain.repository.VehicleRegistrationPolicyRepository;
import com.apten.parkingvehicle.domain.repository.VehicleRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import com.apten.parkingvehicle.infrastructure.kafka.ParkingVehicleOutboxService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 차량 등록, 수정, 승인 흐름을 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class VehicleService {

    // 차량 원본 저장소
    private final VehicleRepository vehicleRepository;

    // 입주민 캐시 저장소
    private final UserCacheRepository userCacheRepository;

    // 세대 캐시 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // 세대 구성원 캐시 저장소
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;

    // 입출차 로그 저장소
    private final ParkingLogRepository parkingLogRepository;

    // 차량 등록 한도 정책 저장소
    private final VehicleRegistrationPolicyRepository vehicleRegistrationPolicyRepository;

    // 차량 상태 변경 outbox 적재 서비스
    private final ParkingVehicleOutboxService parkingVehicleOutboxService;

    // 차량 등록 신청을 처리한다.
    @Transactional
    public VehicleCreateRes createVehicle(VehicleCreateReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 필수 입력값 검증
        String licensePlate = request.getLicensePlate();
        String modelName = request.getModelName();
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        if (modelName == null || modelName.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 세대주 기준 세대 조회 — 세대원이면 여기서 차단
        HouseholdCache household = householdCacheRepository.findByHeadUserId(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 헤더 단지와 세대 단지 일치 검증, 불일치는 세대 미존재와 동일하게 처리
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND);
        }

        // 등록 한도 검사 — 활성 정책이 있을 때만 수행 (정책 미설정 단지는 무제한)
        vehicleRegistrationPolicyRepository.findByComplexIdAndIsActiveTrue(complexId)
                .ifPresent(policy -> {
                    long current = vehicleRepository.countByHouseholdIdAndStatusInAndIsDeletedFalse(
                            household.getId(),
                            List.of(VehicleStatus.PENDING, VehicleStatus.APPROVED));
                    if (current >= policy.getMaxCarCount()) {
                        throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_LIMIT_EXCEEDED);
                    }
                });

        // 단지 기준 차량번호 중복 사전 검사 — 소프트 삭제 이력 포함 (재등록 차단)
        if (vehicleRepository.existsByComplexIdAndLicensePlate(complexId, licensePlate)) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_LICENSE_PLATE);
        }

        // 신규 차량 엔티티 — status/isDeleted는 빌더 디폴트, approvedAt/rejectReason/deletedAt은 null
        Vehicle entity = Vehicle.builder()
                .userId(userId)
                .householdId(household.getId())
                .complexId(complexId)
                .licensePlate(licensePlate)
                .modelName(modelName)
                .vehicleType(request.getVehicleType() != null ? request.getVehicleType() : VehicleType.CAR)
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();

        // 동시 신청으로 사전 검사를 둘 다 통과한 경우 유니크 제약 위반 안전망
        try {
            Vehicle saved = vehicleRepository.save(entity);
            return VehicleCreateRes.builder()
                    .vehicleId(saved.getId())
                    .licensePlate(saved.getLicensePlate())
                    .modelName(saved.getModelName())
                    .vehicleType(saved.getVehicleType())
                    .status(saved.getStatus())
                    .isPrimary(saved.getIsPrimary())
                    .createdAt(saved.getCreatedAt())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_LICENSE_PLATE);
        }
    }

    // 차량 정보를 수정한다.
    @Transactional
    public VehiclePatchRes updateVehicle(Long vehicleId, VehiclePatchReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 차량 단건 + 소유자 동시 검증, 미존재 시 404
        Vehicle vehicle = vehicleRepository.findByIdAndUserIdAndIsDeletedFalse(vehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 요청 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_COMPLEX_MISMATCH);
        }

        // 수정 가능 상태 검증 — PENDING/APPROVED만 허용 (REJECTED/DELETED 차량은 수정 불가)
        VehicleStatus status = vehicle.getStatus();
        if (status != VehicleStatus.PENDING && status != VehicleStatus.APPROVED) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_STATUS_INVALID);
        }

        // 모델명이 명시되면 빈 문자열은 차단 (null은 기존값 유지 의도라 통과)
        if (request.getModelName() != null && request.getModelName().isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 부분 갱신값 계산 — null인 필드는 기존값 유지
        String modelName = request.getModelName() != null ? request.getModelName() : vehicle.getModelName();
        VehicleType vehicleType = request.getVehicleType() != null ? request.getVehicleType() : vehicle.getVehicleType();
        Boolean isPrimary = request.getIsPrimary() != null ? request.getIsPrimary() : vehicle.getIsPrimary();

        // 엔티티 부분 갱신 적용
        vehicle.update(modelName, vehicleType, isPrimary);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        Vehicle saved = vehicleRepository.save(vehicle);

        return VehiclePatchRes.builder()
                .vehicleId(saved.getId())
                .modelName(saved.getModelName())
                .vehicleType(saved.getVehicleType())
                .isPrimary(saved.getIsPrimary())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 차량을 소프트 삭제한다.
    @Transactional
    public VehicleDeleteRes deleteVehicle(Long vehicleId, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 차량 단건 + 소유자 동시 검증, 미존재 시 404
        Vehicle vehicle = vehicleRepository.findByIdAndUserIdAndIsDeletedFalse(vehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 요청 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_COMPLEX_MISMATCH);
        }

        // 소프트 삭제 — isDeleted/deletedAt/status를 markDeleted가 한 번에 처리
        vehicle.markDeleted(LocalDateTime.now());

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        Vehicle saved = vehicleRepository.save(vehicle);

        // 차량 상태 변경 이벤트 outbox 적재 — 모든 상태 삭제에 적재
        parkingVehicleOutboxService.saveVehicleStatusChangedEvent(saved);

        return VehicleDeleteRes.builder()
                .message("차량 삭제 완료")
                .deletedAt(saved.getDeletedAt())
                .build();
    }

    // 내 세대 차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<VehicleListRes> getMyVehicleList(VehicleListReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청자 userId로 활성 세대 매핑 해석 — 미동기화 세대원은 세대 미존재로 처리
        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));
        Long householdId = memberCache.getHouseholdId();

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 상태 필터 유무로 쿼리 메서드 분기 — enum null 비교 회피
        Page<Vehicle> resultPage = request.getStatus() != null
                ? vehicleRepository.findHouseholdVehiclesByStatus(householdId, complexId, request.getStatus(), pageable)
                : vehicleRepository.findHouseholdVehicles(householdId, complexId, pageable);

        List<Vehicle> vehicles = resultPage.getContent();

        // 등록자 이름 batch 조회로 N+1 회피, 캐시 누락 시 이름 null 허용
        List<Long> userIds = vehicles.stream().map(Vehicle::getUserId).distinct().toList();
        Map<Long, UserCache> userCacheMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));

        // 마지막 입출차 로그 batch 조회로 N+1 회피, 동률 로그는 먼저 들어온 한 건만 유지
        List<Long> vehicleIds = vehicles.stream().map(Vehicle::getId).toList();
        Map<Long, ParkingLog> latestLogMap = vehicleIds.isEmpty() ? Map.of() :
                parkingLogRepository.findLatestLogsByVehicleIds(vehicleIds).stream()
                        .collect(Collectors.toMap(ParkingLog::getVehicleId, log -> log, (existing, ignored) -> existing));

        // 응답 DTO 매핑
        List<VehicleListRes> content = vehicles.stream()
                .map(v -> {
                    UserCache userCache = userCacheMap.get(v.getUserId());
                    ParkingLog latestLog = latestLogMap.get(v.getId());
                    return VehicleListRes.builder()
                            .vehicleId(v.getId())
                            .userId(v.getUserId())
                            .residentName(userCache != null ? userCache.getName() : null)
                            .licensePlate(v.getLicensePlate())
                            .modelName(v.getModelName())
                            .vehicleType(v.getVehicleType())
                            .status(v.getStatus())
                            .isPrimary(v.getIsPrimary())
                            .approvedAt(v.getApprovedAt())
                            .createdAt(v.getCreatedAt())
                            .lastLoggedAt(latestLog != null ? latestLog.getLoggedAt() : null)
                            .lastEntryType(latestLog != null ? latestLog.getEntryType() : null)
                            .build();
                })
                .toList();

        return PageResponse.<VehicleListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 내 세대 차량 상세를 조회한다.
    @Transactional(readOnly = true)
    public VehicleDetailRes getMyVehicleDetail(Long vehicleId, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청자 userId로 활성 세대 매핑 해석 — 미동기화 세대원은 세대 미존재로 처리
        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 차량 단건 조회, 미존재 시 404
        Vehicle vehicle = vehicleRepository.findByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 같은 세대 차량이 아니면 존재를 노출하지 않고 404로 차단
        if (!vehicle.getHouseholdId().equals(memberCache.getHouseholdId())) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND);
        }

        // 차량 단지가 요청 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_COMPLEX_MISMATCH);
        }

        // 등록자 이름 조회, 캐시 누락 시 null 허용
        UserCache userCache = userCacheRepository.findById(vehicle.getUserId()).orElse(null);

        // 마지막 입출차 로그 한 건 조회, 기록 없으면 null 허용
        ParkingLog latestLog = parkingLogRepository.findTopByVehicleIdOrderByLoggedAtDesc(vehicle.getId()).orElse(null);

        return VehicleDetailRes.builder()
                .vehicleId(vehicle.getId())
                .userId(vehicle.getUserId())
                .residentName(userCache != null ? userCache.getName() : null)
                .licensePlate(vehicle.getLicensePlate())
                .modelName(vehicle.getModelName())
                .vehicleType(vehicle.getVehicleType())
                .status(vehicle.getStatus())
                .isPrimary(vehicle.getIsPrimary())
                .approvedAt(vehicle.getApprovedAt())
                .rejectReason(vehicle.getRejectReason())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .lastLoggedAt(latestLog != null ? latestLog.getLoggedAt() : null)
                .lastEntryType(latestLog != null ? latestLog.getEntryType() : null)
                .build();
    }

    // 차량번호 중복 여부를 확인한다.
    @Transactional(readOnly = true)
    public LicensePlateCheckRes checkDuplicateLicensePlate(String licensePlate, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 차량번호 필수값 검증
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 기준 차량번호 중복 여부 조회, 소프트 삭제 이력 포함
        boolean isDuplicate = vehicleRepository.existsByComplexIdAndLicensePlate(complexId, licensePlate);

        return LicensePlateCheckRes.builder()
                .isDuplicate(isDuplicate)
                .build();
    }

    // 차량을 승인한다.
    @Transactional
    public VehicleApproveRes approveVehicle(Long vehicleId, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 차량 단건 존재 확인
        Vehicle vehicle = vehicleRepository.findByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 관리자 컨텍스트 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_COMPLEX_MISMATCH);
        }

        // 승인 가능 상태 검증 — PENDING만 허용
        if (vehicle.getStatus() != VehicleStatus.PENDING) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_STATUS_INVALID);
        }

        // 등록 한도 재검사 — 활성 정책이 있을 때 APPROVED 카운트만으로 검사 (관리자 한도 축소 케이스 방어)
        vehicleRegistrationPolicyRepository.findByComplexIdAndIsActiveTrue(targetComplexId)
                .ifPresent(policy -> {
                    long approved = vehicleRepository.countByHouseholdIdAndStatusAndIsDeletedFalse(
                            vehicle.getHouseholdId(), VehicleStatus.APPROVED);
                    if (approved >= policy.getMaxCarCount()) {
                        throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_LIMIT_EXCEEDED);
                    }
                });

        // 승인 상태 반영, rejectReason 초기화
        LocalDateTime now = LocalDateTime.now();
        vehicle.changeStatus(VehicleStatus.APPROVED, now, null);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        Vehicle saved = vehicleRepository.save(vehicle);

        // 차량 상태 변경 이벤트 outbox 적재
        parkingVehicleOutboxService.saveVehicleStatusChangedEvent(saved);

        return VehicleApproveRes.builder()
                .vehicleId(saved.getId())
                .status(saved.getStatus())
                .approvedAt(saved.getApprovedAt())
                .build();
    }

    // 차량을 거절한다.
    @Transactional
    public VehicleRejectRes rejectVehicle(Long vehicleId, VehicleRejectReq request, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 거절 사유 필수값 검증
        String rejectReason = request.getRejectReason();
        if (rejectReason == null || rejectReason.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 차량 단건 존재 확인
        Vehicle vehicle = vehicleRepository.findByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 관리자 컨텍스트 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_COMPLEX_MISMATCH);
        }

        // 거절 가능 상태 검증 — PENDING만 허용
        if (vehicle.getStatus() != VehicleStatus.PENDING) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_STATUS_INVALID);
        }

        // 거절 상태 반영, approvedAt은 명시적으로 null
        vehicle.changeStatus(VehicleStatus.REJECTED, null, rejectReason);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        Vehicle saved = vehicleRepository.save(vehicle);

        // 차량 상태 변경 이벤트 outbox 적재
        parkingVehicleOutboxService.saveVehicleStatusChangedEvent(saved);

        return VehicleRejectRes.builder()
                .vehicleId(saved.getId())
                .status(saved.getStatus())
                .rejectReason(saved.getRejectReason())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 관리자 차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<AdminVehicleListRes> getAdminVehicleList(AdminVehicleListReq request, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 상태 필터 유무로 쿼리 메서드 분기 — enum null 비교 회피
        Page<Vehicle> resultPage = request.getStatus() != null
                ? vehicleRepository.findAdminVehiclesByStatus(targetComplexId, request.getStatus(),
                        request.getBuilding(), request.getUnit(), request.getKeyword(), pageable)
                : vehicleRepository.findAdminVehicles(targetComplexId,
                        request.getBuilding(), request.getUnit(), request.getKeyword(), pageable);

        List<Vehicle> vehicles = resultPage.getContent();

        // 캐시 batch 조회로 N+1 회피
        List<Long> userIds = vehicles.stream().map(Vehicle::getUserId).distinct().toList();
        List<Long> householdIds = vehicles.stream().map(Vehicle::getHouseholdId).distinct().toList();
        Map<Long, UserCache> userCacheMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));
        Map<Long, HouseholdCache> householdCacheMap = householdIds.isEmpty() ? Map.of() :
                householdCacheRepository.findAllById(householdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getId, h -> h));

        // 응답 DTO 매핑, 캐시 누락 시 해당 필드 null
        List<AdminVehicleListRes> content = vehicles.stream()
                .map(v -> {
                    UserCache userCache = userCacheMap.get(v.getUserId());
                    HouseholdCache householdCache = householdCacheMap.get(v.getHouseholdId());
                    return AdminVehicleListRes.builder()
                            .vehicleId(v.getId())
                            .licensePlate(v.getLicensePlate())
                            .modelName(v.getModelName())
                            .vehicleType(v.getVehicleType())
                            .status(v.getStatus())
                            .residentName(userCache != null ? userCache.getName() : null)
                            .building(householdCache != null ? householdCache.getBuilding() : null)
                            .unit(householdCache != null ? householdCache.getUnit() : null)
                            .createdAt(v.getCreatedAt())
                            .build();
                })
                .toList();

        return PageResponse.<AdminVehicleListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 관리자 차량 상세를 조회한다.
    @Transactional(readOnly = true)
    public AdminVehicleDetailRes getAdminVehicleDetail(Long vehicleId, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 차량 단건 존재 확인
        Vehicle vehicle = vehicleRepository.findByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 관리자 컨텍스트 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_COMPLEX_MISMATCH);
        }

        // 입주민과 세대 캐시 조회, 누락 시 null
        UserCache userCache = userCacheRepository.findById(vehicle.getUserId()).orElse(null);
        HouseholdCache householdCache = householdCacheRepository.findById(vehicle.getHouseholdId()).orElse(null);

        return AdminVehicleDetailRes.builder()
                .vehicleId(vehicle.getId())
                .householdId(vehicle.getHouseholdId())
                .userId(vehicle.getUserId())
                .residentName(userCache != null ? userCache.getName() : null)
                .building(householdCache != null ? householdCache.getBuilding() : null)
                .unit(householdCache != null ? householdCache.getUnit() : null)
                .licensePlate(vehicle.getLicensePlate())
                .modelName(vehicle.getModelName())
                .vehicleType(vehicle.getVehicleType())
                .status(vehicle.getStatus())
                .isPrimary(vehicle.getIsPrimary())
                .approvedAt(vehicle.getApprovedAt())
                .rejectReason(vehicle.getRejectReason())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

}
