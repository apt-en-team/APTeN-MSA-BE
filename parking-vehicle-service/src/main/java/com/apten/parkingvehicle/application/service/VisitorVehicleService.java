package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleReRegisterReq;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCancelRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleReRegisterRes;
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.UserCache;
import com.apten.parkingvehicle.domain.entity.VisitorVehicle;
import com.apten.parkingvehicle.domain.enums.ParkingTargetDateType;
import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.UserCacheRepository;
import com.apten.parkingvehicle.domain.repository.VisitorVehicleRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import java.time.LocalDate;
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

// 방문차량 등록, 조회, 만료 흐름을 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class VisitorVehicleService {

    // 방문차량 원본 저장소
    private final VisitorVehicleRepository visitorVehicleRepository;

    // 입주민 캐시 저장소
    private final UserCacheRepository userCacheRepository;

    // 세대 캐시 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // 방문차량을 등록한다.
    public VisitorVehicleCreateRes createVisitorVehicle(VisitorVehicleCreateReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문 예정일 유효성 검증
        //TODO 사용자 소속 세대 확인
        //TODO APPROVED 상태로 저장
        //TODO 상태 변경 이벤트 또는 집계 대상 여부 확인
        return VisitorVehicleCreateRes.builder()
                .visitorVehicleId(null)
                .licensePlate(request.getLicensePlate())
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(VisitorVehicleStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 내 방문차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<VisitorVehicleListRes> getMyVisitorVehicleList(VisitorVehicleListReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 상태 필터 유무로 쿼리 메서드 분기 — enum null 비교 회피, 기간 조건은 양쪽 모두 동적 처리
        Page<VisitorVehicle> resultPage = request.getStatus() != null
                ? visitorVehicleRepository.findMyVisitorVehiclesByStatus(
                        userId, complexId, request.getStatus(),
                        request.getFromDate(), request.getToDate(), pageable)
                : visitorVehicleRepository.findMyVisitorVehicles(
                        userId, complexId,
                        request.getFromDate(), request.getToDate(), pageable);

        // 응답 DTO 매핑
        List<VisitorVehicleListRes> content = resultPage.getContent().stream()
                .map(v -> VisitorVehicleListRes.builder()
                        .visitorVehicleId(v.getId())
                        .licensePlate(v.getLicensePlate())
                        .visitorName(v.getVisitorName())
                        .phone(v.getPhone())
                        .visitDate(v.getVisitDate())
                        .startTime(v.getStartTime())
                        .endTime(v.getEndTime())
                        .status(v.getStatus())
                        .createdAt(v.getCreatedAt())
                        .build())
                .toList();

        return PageResponse.<VisitorVehicleListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 내 방문차량 상세를 조회한다.
    @Transactional(readOnly = true)
    public VisitorVehicleDetailRes getMyVisitorVehicleDetail(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        VisitorVehicle visitorVehicle = visitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(visitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!visitorVehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        return VisitorVehicleDetailRes.builder()
                .visitorVehicleId(visitorVehicle.getId())
                .licensePlate(visitorVehicle.getLicensePlate())
                .visitorName(visitorVehicle.getVisitorName())
                .phone(visitorVehicle.getPhone())
                .visitDate(visitorVehicle.getVisitDate())
                .startTime(visitorVehicle.getStartTime())
                .endTime(visitorVehicle.getEndTime())
                .status(visitorVehicle.getStatus())
                .sourceId(visitorVehicle.getSourceId())
                .createdAt(visitorVehicle.getCreatedAt())
                .updatedAt(visitorVehicle.getUpdatedAt())
                .build();
    }

    // 방문차량 정보를 수정한다.
    public VisitorVehiclePatchRes updateVisitorVehicle(Long visitorVehicleId, VisitorVehiclePatchReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문 예정일 유효성 검증
        //TODO 사용자 소속 세대 확인
        //TODO 방문차량 소유자 검증
        //TODO APPROVED 상태 차량만 수정 가능 여부 확인
        return VisitorVehiclePatchRes.builder()
                .visitorVehicleId(visitorVehicleId)
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량을 취소한다.
    public VisitorVehicleCancelRes cancelVisitorVehicle(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문차량 존재 여부 확인
        //TODO 사용자 소속 세대 확인
        //TODO APPROVED/CANCELLED/DELETED 상태 처리
        //TODO 상태 변경 이벤트 또는 집계 대상 여부 확인
        return VisitorVehicleCancelRes.builder()
                .visitorVehicleId(visitorVehicleId)
                .status(VisitorVehicleStatus.CANCELLED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량을 삭제한다.
    public VisitorVehicleDeleteRes deleteVisitorVehicle(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문차량 존재 여부 확인
        //TODO 사용자 소속 세대 확인
        //TODO APPROVED/CANCELLED/DELETED 상태 처리
        return VisitorVehicleDeleteRes.builder()
                .message("방문차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량을 재등록한다.
    public VisitorVehicleReRegisterRes reRegisterVisitorVehicle(Long visitorVehicleId, VisitorVehicleReRegisterReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 기존 방문차량 존재 여부 확인
        //TODO 방문 예정일 유효성 검증
        //TODO 사용자 소속 세대 확인
        //TODO sourceId를 유지한 신규 방문차량 등록
        return VisitorVehicleReRegisterRes.builder()
                .visitorVehicleId(null)
                .sourceVisitorVehicleId(visitorVehicleId)
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(VisitorVehicleStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자가 방문차량을 등록한다.
    public AdminVisitorVehicleCreateRes createAdminVisitorVehicle(AdminVisitorVehicleCreateReq request, String userRole, Long complexId, Long selectedComplexId) {
        //TODO 관리자 컨텍스트 해석
        //TODO 세대 존재 여부 확인
        //TODO 방문 예정일 유효성 검증
        //TODO 관리자가 지정한 세대로 방문차량 등록
        return AdminVisitorVehicleCreateRes.builder()
                .visitorVehicleId(null)
                .householdId(request.getHouseholdId())
                .licensePlate(request.getLicensePlate())
                .visitDate(request.getVisitDate())
                .status(VisitorVehicleStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 방문 예정 차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<AdminVisitorVehicleListRes> getAdminVisitorVehicleList(AdminVisitorVehicleListReq request, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // targetDateType 기준 기간 조건 계산 — TODAY는 오늘 단일, TOMORROW는 내일 단일, ALL과 null은 기간 무필터
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (request.getTargetDateType() == ParkingTargetDateType.TODAY) {
            LocalDate today = LocalDate.now();
            fromDate = today;
            toDate = today;
        } else if (request.getTargetDateType() == ParkingTargetDateType.TOMORROW) {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            fromDate = tomorrow;
            toDate = tomorrow;
        }

        // 단지/기간/키워드 동적 필터
        Page<VisitorVehicle> resultPage = visitorVehicleRepository.findAdminVisitorVehicles(
                targetComplexId, fromDate, toDate, request.getKeyword(), pageable);

        List<VisitorVehicle> vehicles = resultPage.getContent();

        // 세대 캐시 batch 조회로 N+1 회피
        List<Long> householdIds = vehicles.stream().map(VisitorVehicle::getHouseholdId).distinct().toList();
        Map<Long, HouseholdCache> householdCacheMap = householdIds.isEmpty() ? Map.of() :
                householdCacheRepository.findAllById(householdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getId, h -> h));

        // 응답 DTO 매핑, 캐시 누락 시 해당 필드 null
        List<AdminVisitorVehicleListRes> content = vehicles.stream()
                .map(v -> {
                    HouseholdCache householdCache = householdCacheMap.get(v.getHouseholdId());
                    return AdminVisitorVehicleListRes.builder()
                            .visitorVehicleId(v.getId())
                            .licensePlate(v.getLicensePlate())
                            .visitorName(v.getVisitorName())
                            .phone(v.getPhone())
                            .visitDate(v.getVisitDate())
                            .startTime(v.getStartTime())
                            .endTime(v.getEndTime())
                            .building(householdCache != null ? householdCache.getBuilding() : null)
                            .unit(householdCache != null ? householdCache.getUnit() : null)
                            .status(v.getStatus())
                            .createdAt(v.getCreatedAt())
                            .build();
                })
                .toList();

        return PageResponse.<AdminVisitorVehicleListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 관리자 방문차량 상세를 조회한다.
    @Transactional(readOnly = true)
    public AdminVisitorVehicleDetailRes getAdminVisitorVehicleDetail(Long visitorVehicleId, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 방문차량 단건 존재 확인
        VisitorVehicle visitorVehicle = visitorVehicleRepository.findByIdAndIsDeletedFalse(visitorVehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 방문차량 단지가 관리자 컨텍스트 단지와 다르면 단지 불일치로 접근 차단
        if (!visitorVehicle.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        // 입주민과 세대 캐시 조회, 누락 시 null
        UserCache userCache = userCacheRepository.findById(visitorVehicle.getUserId()).orElse(null);
        HouseholdCache householdCache = householdCacheRepository.findById(visitorVehicle.getHouseholdId()).orElse(null);

        return AdminVisitorVehicleDetailRes.builder()
                .visitorVehicleId(visitorVehicle.getId())
                .householdId(visitorVehicle.getHouseholdId())
                .userId(visitorVehicle.getUserId())
                .residentName(userCache != null ? userCache.getName() : null)
                .building(householdCache != null ? householdCache.getBuilding() : null)
                .unit(householdCache != null ? householdCache.getUnit() : null)
                .licensePlate(visitorVehicle.getLicensePlate())
                .visitorName(visitorVehicle.getVisitorName())
                .phone(visitorVehicle.getPhone())
                .visitDate(visitorVehicle.getVisitDate())
                .startTime(visitorVehicle.getStartTime())
                .endTime(visitorVehicle.getEndTime())
                .status(visitorVehicle.getStatus())
                .sourceId(visitorVehicle.getSourceId())
                .createdAt(visitorVehicle.getCreatedAt())
                .updatedAt(visitorVehicle.getUpdatedAt())
                .build();
    }

    // 방문차량 자동 만료를 처리한다.
    public VisitorVehicleExpireRes expireVisitorVehicles() {
        //TODO visitDate가 지난 APPROVED 방문차량 조회
        //TODO EXPIRED 상태로 변경
        //TODO 만료 처리 건수 반환
        return VisitorVehicleExpireRes.builder()
                .expiredCount(0)
                .executedAt(LocalDateTime.now())
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
