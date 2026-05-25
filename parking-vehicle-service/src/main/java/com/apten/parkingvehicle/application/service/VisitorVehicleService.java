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
import java.time.LocalTime;
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
    @Transactional
    public VisitorVehicleCreateRes createVisitorVehicle(VisitorVehicleCreateReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 차량번호 필수값 검증
        String licensePlate = request.getLicensePlate();
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문 시간대 필수값 검증
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문 예정일 유효성 검증 — null과 과거 날짜 거부, 같은 날은 허용
        LocalDate visitDate = request.getVisitDate();
        if (visitDate == null || visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISIT_DATE_INVALID);
        }

        // 세대주 기준 세대 조회 — 세대원이면 여기서 차단
        HouseholdCache household = householdCacheRepository.findByHeadUserId(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 헤더 단지와 세대 단지 일치 검증, 불일치는 세대 미존재와 동일하게 처리
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND);
        }

        // 신규 방문차량 엔티티 — status는 빌더 디폴트(APPROVED), sourceId는 null, isDeleted는 false
        VisitorVehicle entity = VisitorVehicle.builder()
                .userId(userId)
                .householdId(household.getId())
                .complexId(complexId)
                .licensePlate(licensePlate)
                .visitorName(request.getVisitorName())
                .phone(request.getPhone())
                .visitDate(visitDate)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        VisitorVehicle saved = visitorVehicleRepository.save(entity);

        return VisitorVehicleCreateRes.builder()
                .visitorVehicleId(saved.getId())
                .licensePlate(saved.getLicensePlate())
                .visitDate(saved.getVisitDate())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
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
    @Transactional
    public VisitorVehiclePatchRes updateVisitorVehicle(Long visitorVehicleId, VisitorVehiclePatchReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        VisitorVehicle visitorVehicle = visitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(visitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!visitorVehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        // 수정 가능 상태 검증 — APPROVED만 허용
        if (visitorVehicle.getStatus() != VisitorVehicleStatus.APPROVED) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_STATUS_INVALID);
        }

        // 기존 방문 시작 일시가 이미 지났으면 수정 불가 (startTime null이면 visitDate 자정으로 폴백)
        LocalTime existingStartTime = visitorVehicle.getStartTime();
        LocalDateTime visitStart = existingStartTime != null
                ? LocalDateTime.of(visitorVehicle.getVisitDate(), existingStartTime)
                : visitorVehicle.getVisitDate().atStartOfDay();
        if (!visitStart.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_STATUS_INVALID);
        }

        // 방문 예정일 유효성 검증 — 수정 시 visitDate는 필수 입력, null과 과거 날짜 거부
        LocalDate visitDate = request.getVisitDate();
        if (visitDate == null || visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISIT_DATE_INVALID);
        }

        // 부분 갱신값 계산 — visitDate 외 null 필드는 기존값 유지
        String visitorName = request.getVisitorName() != null ? request.getVisitorName() : visitorVehicle.getVisitorName();
        String phone = request.getPhone() != null ? request.getPhone() : visitorVehicle.getPhone();
        LocalTime startTime = request.getStartTime() != null ? request.getStartTime() : visitorVehicle.getStartTime();
        LocalTime endTime = request.getEndTime() != null ? request.getEndTime() : visitorVehicle.getEndTime();

        // 엔티티 부분 갱신 적용
        visitorVehicle.update(visitorName, phone, visitDate, startTime, endTime);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        VisitorVehicle saved = visitorVehicleRepository.save(visitorVehicle);

        return VisitorVehiclePatchRes.builder()
                .visitorVehicleId(saved.getId())
                .visitDate(saved.getVisitDate())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 방문차량을 취소한다.
    @Transactional
    public VisitorVehicleCancelRes cancelVisitorVehicle(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        VisitorVehicle visitorVehicle = visitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(visitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!visitorVehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        // 취소 가능 상태 검증 — APPROVED만 허용 (중복 취소·만료 후 취소 차단)
        if (visitorVehicle.getStatus() != VisitorVehicleStatus.APPROVED) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_STATUS_INVALID);
        }

        // 취소 상태 반영
        visitorVehicle.changeStatus(VisitorVehicleStatus.CANCELLED);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        VisitorVehicle saved = visitorVehicleRepository.save(visitorVehicle);

        return VisitorVehicleCancelRes.builder()
                .visitorVehicleId(saved.getId())
                .status(saved.getStatus())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 방문차량을 삭제한다.
    @Transactional
    public VisitorVehicleDeleteRes deleteVisitorVehicle(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        VisitorVehicle visitorVehicle = visitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(visitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!visitorVehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        // 소프트 삭제 — isDeleted/deletedAt/status를 markDeleted가 한 번에 처리, 상태 검증 없음
        visitorVehicle.markDeleted(LocalDateTime.now());

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        VisitorVehicle saved = visitorVehicleRepository.save(visitorVehicle);

        return VisitorVehicleDeleteRes.builder()
                .message("방문차량 삭제 완료")
                .deletedAt(saved.getDeletedAt())
                .build();
    }

    // 방문차량을 재등록한다.
    @Transactional
    public VisitorVehicleReRegisterRes reRegisterVisitorVehicle(Long visitorVehicleId, VisitorVehicleReRegisterReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 원본 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        VisitorVehicle source = visitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(visitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 원본 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!source.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        // 방문 시간대 필수값 검증
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문 예정일 유효성 검증 — null과 과거 날짜 거부, 같은 날은 허용
        LocalDate visitDate = request.getVisitDate();
        if (visitDate == null || visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISIT_DATE_INVALID);
        }

        // 신규 방문차량 엔티티 — 식별/연락 정보는 원본 승계, 일정은 요청값, sourceId는 원본 ID
        VisitorVehicle entity = VisitorVehicle.builder()
                .userId(source.getUserId())
                .householdId(source.getHouseholdId())
                .complexId(source.getComplexId())
                .licensePlate(source.getLicensePlate())
                .visitorName(source.getVisitorName())
                .phone(source.getPhone())
                .visitDate(visitDate)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .sourceId(source.getId())
                .build();

        VisitorVehicle saved = visitorVehicleRepository.save(entity);

        return VisitorVehicleReRegisterRes.builder()
                .visitorVehicleId(saved.getId())
                .sourceVisitorVehicleId(source.getId())
                .licensePlate(saved.getLicensePlate())
                .visitDate(saved.getVisitDate())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 관리자가 방문차량을 등록한다.
    @Transactional
    public AdminVisitorVehicleCreateRes createAdminVisitorVehicle(AdminVisitorVehicleCreateReq request, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 차량번호 필수값 검증
        String licensePlate = request.getLicensePlate();
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 세대 식별자 필수값 검증
        Long householdId = request.getHouseholdId();
        if (householdId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문 시간대 필수값 검증
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문 예정일 유효성 검증 — null과 과거 날짜 거부, 같은 날은 허용
        LocalDate visitDate = request.getVisitDate();
        if (visitDate == null || visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISIT_DATE_INVALID);
        }

        // 요청 세대 조회
        HouseholdCache household = householdCacheRepository.findById(householdId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 세대 단지가 관리자 컨텍스트 단지와 다르면 세대 미존재로 처리
        if (!household.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND);
        }

        // 세대주 userId 확보 — 세대주 정보 미수신이면 사용자 미존재로 처리
        Long headUserId = household.getHeadUserId();
        if (headUserId == null) {
            throw new BusinessException(ParkingVehicleErrorCode.USER_NOT_FOUND);
        }

        // 신규 방문차량 엔티티 — userId는 세대주, status는 빌더 디폴트(APPROVED), sourceId는 null
        VisitorVehicle entity = VisitorVehicle.builder()
                .userId(headUserId)
                .householdId(householdId)
                .complexId(targetComplexId)
                .licensePlate(licensePlate)
                .visitorName(request.getVisitorName())
                .phone(request.getPhone())
                .visitDate(visitDate)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .memo(request.getMemo())
                .build();

        VisitorVehicle saved = visitorVehicleRepository.save(entity);

        return AdminVisitorVehicleCreateRes.builder()
                .visitorVehicleId(saved.getId())
                .householdId(saved.getHouseholdId())
                .licensePlate(saved.getLicensePlate())
                .visitDate(saved.getVisitDate())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
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
