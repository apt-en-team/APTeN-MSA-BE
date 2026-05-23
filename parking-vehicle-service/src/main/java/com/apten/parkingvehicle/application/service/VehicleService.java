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
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.UserCache;
import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.UserCacheRepository;
import com.apten.parkingvehicle.domain.repository.VehicleRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    // 차량 등록 신청을 처리한다.
    public VehicleCreateRes createVehicle(VehicleCreateReq request, Long userId, String userRole, Long complexId) {
        //TODO user_cache에서 로그인 사용자 확인
        //TODO household_cache에서 사용자 소속 세대 확인
        //TODO 차량번호 중복 여부 확인
        //TODO 차량 정책 기준 등록 제한 검증
        //TODO PENDING 상태 차량 저장
        //TODO 필요 시 알림/이벤트 outbox 적재
        return VehicleCreateRes.builder()
                .vehicleId(null)
                .licensePlate(request.getLicensePlate())
                .modelName(request.getModelName())
                .vehicleType(request.getVehicleType() != null ? request.getVehicleType() : VehicleType.CAR)
                .status(VehicleStatus.PENDING)
                .isPrimary(request.getIsPrimary())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 차량 정보를 수정한다.
    public VehiclePatchRes updateVehicle(Long vehicleId, VehiclePatchReq request, Long userId, String userRole, Long complexId) {
        //TODO 로그인 사용자 확인
        //TODO 차량 존재 여부 확인
        //TODO 차량 소유자 검증
        //TODO 수정 가능한 상태인지 확인
        //TODO 차량 기본 정보 수정
        return VehiclePatchRes.builder()
                .vehicleId(vehicleId)
                .modelName(request.getModelName())
                .vehicleType(request.getVehicleType())
                .isPrimary(request.getIsPrimary())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 차량을 소프트 삭제한다.
    public VehicleDeleteRes deleteVehicle(Long vehicleId, Long userId, String userRole, Long complexId) {
        //TODO 차량 존재 여부 확인
        //TODO 차량 소유자 검증
        //TODO DELETED 상태 및 isDeleted 처리
        //TODO 승인 차량 삭제 시 차량 상태 변경 이벤트 outbox 적재
        return VehicleDeleteRes.builder()
                .message("차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 내 차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<VehicleListRes> getMyVehicleList(VehicleListReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 상태 필터 유무로 쿼리 메서드 분기 — enum null 비교 회피
        Page<Vehicle> resultPage = request.getStatus() != null
                ? vehicleRepository.findMyVehiclesByStatus(userId, complexId, request.getStatus(), pageable)
                : vehicleRepository.findMyVehicles(userId, complexId, pageable);

        // 응답 DTO 매핑
        List<VehicleListRes> content = resultPage.getContent().stream()
                .map(v -> VehicleListRes.builder()
                        .vehicleId(v.getId())
                        .licensePlate(v.getLicensePlate())
                        .modelName(v.getModelName())
                        .vehicleType(v.getVehicleType())
                        .status(v.getStatus())
                        .isPrimary(v.getIsPrimary())
                        .approvedAt(v.getApprovedAt())
                        .createdAt(v.getCreatedAt())
                        .build())
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

    // 내 차량 상세를 조회한다.
    @Transactional(readOnly = true)
    public VehicleDetailRes getMyVehicleDetail(Long vehicleId, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 차량 단건 + 소유자 동시 검증, 미존재 시 404
        Vehicle vehicle = vehicleRepository.findByIdAndUserIdAndIsDeletedFalse(vehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 요청 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_OWNER_MISMATCH);
        }

        return VehicleDetailRes.builder()
                .vehicleId(vehicle.getId())
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

    // 차량번호 중복 여부를 확인한다.
    public LicensePlateCheckRes checkDuplicateLicensePlate(String licensePlate, Long userId, String userRole, Long complexId) {
        //TODO 로그인 사용자 또는 관리자 컨텍스트에서 complexId 확인
        //TODO 단지 기준 차량번호 중복 여부 확인
        return LicensePlateCheckRes.builder()
                .isDuplicate(false)
                .build();
    }

    // 차량을 승인한다.
    public VehicleApproveRes approveVehicle(Long vehicleId, String userRole, Long complexId, Long selectedComplexId) {
        //TODO 차량 존재 여부 확인
        //TODO 차량 상태가 PENDING인지 확인
        //TODO 현재 승인 차량 수 조회
        //TODO vehicle_policy 기준 승인 가능 여부 확인
        //TODO APPROVED 상태 변경 및 approvedAt 저장
        //TODO 차량 상태 변경 이벤트 outbox 적재
        return VehicleApproveRes.builder()
                .vehicleId(vehicleId)
                .status(VehicleStatus.APPROVED)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    // 차량을 거절한다.
    public VehicleRejectRes rejectVehicle(Long vehicleId, VehicleRejectReq request, String userRole, Long complexId, Long selectedComplexId) {
        //TODO 차량 존재 여부 확인
        //TODO 차량 상태가 PENDING인지 확인
        //TODO REJECTED 상태 변경 및 거절 사유 저장
        //TODO 차량 상태 변경 이벤트 outbox 적재
        return VehicleRejectRes.builder()
                .vehicleId(vehicleId)
                .status(VehicleStatus.REJECTED)
                .rejectReason(request.getRejectReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<AdminVehicleListRes> getAdminVehicleList(AdminVehicleListReq request, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

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
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 차량 단건 존재 확인
        Vehicle vehicle = vehicleRepository.findByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_NOT_FOUND));

        // 차량 단지가 관리자 컨텍스트 단지와 다르면 권한 차단
        if (!vehicle.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_OWNER_MISMATCH);
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

    // 입주민 API 헤더 컨텍스트 검증, USER 권한과 식별자 필수
    private void validateResidentContext(Long userId, String userRole, Long complexId) {
        // userRole 빈값 검증
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // USER 권한 아니면 입주민 API 차단
        if (!"USER".equals(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        // userId, complexId 필수값 검증
        if (userId == null || complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 관리자 주차 API의 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    // MASTER는 선택 단지, 일반 관리자는 토큰 단지 기준으로 처리한다.
    private Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if ("MASTER".equals(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
