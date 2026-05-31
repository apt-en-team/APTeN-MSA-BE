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
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleStatsRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCancelRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleReRegisterRes;
import com.apten.parkingvehicle.application.support.RoleContextValidator;
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.HouseholdMemberCache;
import com.apten.parkingvehicle.domain.entity.UserCache;
import com.apten.parkingvehicle.domain.entity.VisitorVehicle;
import com.apten.parkingvehicle.domain.enums.ParkingTargetDateType;
import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.HouseholdMemberCacheRepository;
import com.apten.parkingvehicle.domain.repository.UserCacheRepository;
import com.apten.parkingvehicle.domain.repository.VisitorVehicleRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import com.apten.parkingvehicle.infrastructure.kafka.ParkingVehicleOutboxService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // 세대 구성원 캐시 저장소
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;

    // 방문차량 알림 outbox 적재 서비스
    private final ParkingVehicleOutboxService parkingVehicleOutboxService;

    // 미출차 방문차량 만료 유예 일수 (visitDate 기준 +N일까지 출차 없으면 EXPIRED 처리)
    @Value("${apten.policy.visitor-vehicle.expire-grace-days:3}")
    private int expireGraceDays;

    // 방문차량을 등록한다.
    @Transactional
    public VisitorVehicleCreateRes createVisitorVehicle(VisitorVehicleCreateReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 차량번호 필수값 검증
        String licensePlate = request.getLicensePlate();
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 방문 예정일 유효성 검증 — null과 과거 날짜 거부, 같은 날은 허용
        LocalDate visitDate = request.getVisitDate();
        if (visitDate == null || visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISIT_DATE_INVALID);
        }

        // 세대 구성원 기준 세대 해석 — 세대주와 세대원 모두 본인 세대로 등록 가능
        Long householdId = resolveHouseholdId(userId);
        HouseholdCache household = householdCacheRepository.findById(householdId)
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
                .visitPurpose(request.getVisitPurpose())
                .build();

        VisitorVehicle saved = visitorVehicleRepository.save(entity);

        // 방문차량 등록 알림 outbox 적재 — 관리자 대상
        parkingVehicleOutboxService.saveVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.VISITOR_VEHICLE_REGISTERED,
                ParkingVehicleOutboxService.TARGET_ADMIN,
                userId);

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
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 요청자 userId로 활성 세대 해석 — 미동기화 세대원은 세대 미존재로 처리
        Long householdId = resolveHouseholdId(userId);

        // 상태 필터 유무로 쿼리 메서드 분기 — enum null 비교 회피, 기간 조건은 양쪽 모두 동적 처리
        Page<VisitorVehicle> resultPage = request.getStatus() != null
                ? visitorVehicleRepository.findHouseholdVisitorVehiclesByStatus(
                        householdId, complexId, request.getStatus(),
                        request.getFromDate(), request.getToDate(), pageable)
                : visitorVehicleRepository.findHouseholdVisitorVehicles(
                        householdId, complexId,
                        request.getFromDate(), request.getToDate(), pageable);

        // 응답 DTO 매핑
        List<VisitorVehicleListRes> content = resultPage.getContent().stream()
                .map(v -> VisitorVehicleListRes.builder()
                        .visitorVehicleId(v.getId())
                        .licensePlate(v.getLicensePlate())
                        .visitorName(v.getVisitorName())
                        .phone(v.getPhone())
                        .visitPurpose(v.getVisitPurpose())
                        .visitDate(v.getVisitDate())
                        .startTime(v.getStartTime())
                        .endTime(v.getEndTime())
                        .status(v.getStatus())
                        .createdAt(v.getCreatedAt())
                        .isMine(v.getUserId().equals(userId))
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
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청자 userId로 활성 세대 해석 — 미동기화 세대원은 세대 미존재로 처리
        Long householdId = resolveHouseholdId(userId);

        // 방문차량 단건 조회, 미존재 시 404
        VisitorVehicle visitorVehicle = visitorVehicleRepository.findByIdAndIsDeletedFalse(visitorVehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 같은 세대 방문차량이 아니면 존재를 노출하지 않고 404로 차단
        if (!visitorVehicle.getHouseholdId().equals(householdId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND);
        }

        // 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!visitorVehicle.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        return VisitorVehicleDetailRes.builder()
                .visitorVehicleId(visitorVehicle.getId())
                .licensePlate(visitorVehicle.getLicensePlate())
                .visitorName(visitorVehicle.getVisitorName())
                .phone(visitorVehicle.getPhone())
                .visitPurpose(visitorVehicle.getVisitPurpose())
                .visitDate(visitorVehicle.getVisitDate())
                .startTime(visitorVehicle.getStartTime())
                .endTime(visitorVehicle.getEndTime())
                .status(visitorVehicle.getStatus())
                .sourceId(visitorVehicle.getSourceId())
                .createdAt(visitorVehicle.getCreatedAt())
                .updatedAt(visitorVehicle.getUpdatedAt())
                .isMine(visitorVehicle.getUserId().equals(userId))
                .build();
    }

    // 방문차량 정보를 수정한다.
    @Transactional
    public VisitorVehiclePatchRes updateVisitorVehicle(Long visitorVehicleId, VisitorVehiclePatchReq request, Long userId, String userRole, Long complexId) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

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

        // 기존 방문 예정일이 이미 지났으면 수정 불가
        if (visitorVehicle.getVisitDate().isBefore(LocalDate.now())) {
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
        String visitPurpose = request.getVisitPurpose() != null ? request.getVisitPurpose() : visitorVehicle.getVisitPurpose();

        // 엔티티 부분 갱신 적용, 시간은 기존값 그대로 전달
        visitorVehicle.update(visitorName, phone, visitDate, visitorVehicle.getStartTime(), visitorVehicle.getEndTime(), visitPurpose);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        VisitorVehicle saved = visitorVehicleRepository.save(visitorVehicle);

        // 방문차량 수정 알림 outbox 적재 — 관리자 대상
        parkingVehicleOutboxService.saveVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.VISITOR_VEHICLE_UPDATED,
                ParkingVehicleOutboxService.TARGET_ADMIN,
                userId);

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
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

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

        // 방문차량 취소 알림 outbox 적재 — 관리자 대상
        parkingVehicleOutboxService.saveVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.VISITOR_VEHICLE_CANCELLED,
                ParkingVehicleOutboxService.TARGET_ADMIN,
                userId);

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
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

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
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 요청자 userId로 활성 세대 해석 — 미동기화 세대원은 세대 미존재로 처리
        Long householdId = resolveHouseholdId(userId);

        // 원본 방문차량 단건 조회, 미존재 시 404
        VisitorVehicle source = visitorVehicleRepository.findByIdAndIsDeletedFalse(visitorVehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND));

        // 같은 세대 방문차량이 아니면 존재를 노출하지 않고 404로 차단
        if (!source.getHouseholdId().equals(householdId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_NOT_FOUND);
        }

        // 원본 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!source.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.VISITOR_VEHICLE_COMPLEX_MISMATCH);
        }

        // 방문 예정일 유효성 검증 — null과 과거 날짜 거부, 같은 날은 허용
        LocalDate visitDate = request.getVisitDate();
        if (visitDate == null || visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ParkingVehicleErrorCode.VISIT_DATE_INVALID);
        }

        // 신규 방문차량 엔티티 — userId는 재등록 실행자, 식별/연락 정보는 원본 승계, 일정은 요청값, sourceId는 원본 ID
        VisitorVehicle entity = VisitorVehicle.builder()
                .userId(userId)
                .householdId(source.getHouseholdId())
                .complexId(source.getComplexId())
                .licensePlate(source.getLicensePlate())
                .visitorName(source.getVisitorName())
                .phone(source.getPhone())
                .visitDate(visitDate)
                .visitPurpose(source.getVisitPurpose())
                .sourceId(source.getId())
                .build();

        VisitorVehicle saved = visitorVehicleRepository.save(entity);

        // 방문차량 재등록 알림 outbox 적재 — 관리자 대상
        parkingVehicleOutboxService.saveVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.VISITOR_VEHICLE_RE_REGISTERED,
                ParkingVehicleOutboxService.TARGET_ADMIN,
                userId);

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
    public AdminVisitorVehicleCreateRes createAdminVisitorVehicle(AdminVisitorVehicleCreateReq request, Long actorUserId, String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

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
                .visitPurpose(request.getVisitPurpose())
                .memo(request.getMemo())
                .build();

        VisitorVehicle saved = visitorVehicleRepository.save(entity);

        // 관리자 대리 등록 알림 outbox 적재 — 세대 대상, actorUserId는 컨트롤러가 전달한 관리자 식별자
        parkingVehicleOutboxService.saveVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.VISITOR_VEHICLE_CREATED_BY_ADMIN,
                ParkingVehicleOutboxService.TARGET_HOUSEHOLD,
                actorUserId);

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
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

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
                            .visitPurpose(v.getVisitPurpose())
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
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

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
                .visitPurpose(visitorVehicle.getVisitPurpose())
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
    // 출차 기록 없이 유예 기간이 지난 APPROVED 방문차량을 EXPIRED로 일괄 전환한다.
    @Transactional
    public VisitorVehicleExpireRes expireVisitorVehicles() {
        // visitDate < (오늘 - 유예일) 기준으로 미출차 APPROVED 방문차량 조회
        LocalDate cutoff = LocalDate.now().minusDays(expireGraceDays);
        List<VisitorVehicle> targets = visitorVehicleRepository
                .findByVisitDateBeforeAndStatusAndIsDeletedFalse(cutoff, VisitorVehicleStatus.APPROVED);

        // 상태 일괄 전환, dirty checking 명시화
        for (VisitorVehicle target : targets) {
            target.changeStatus(VisitorVehicleStatus.EXPIRED);
        }
        if (!targets.isEmpty()) {
            visitorVehicleRepository.saveAll(targets);
        }

        return VisitorVehicleExpireRes.builder()
                .expiredCount(targets.size())
                .executedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 방문차량 통계를 조회한다.
    @Transactional(readOnly = true)
    public AdminVisitorVehicleStatsRes getAdminVisitorVehicleStats(String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 기준 날짜와 이번 달 범위 계산
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        // 오늘 방문 예정 건수 집계, 등록완료 상태만
        long todayScheduled = visitorVehicleRepository
                .countByComplexIdAndVisitDateAndStatusAndIsDeletedFalse(targetComplexId, today, VisitorVehicleStatus.APPROVED);

        // 앞으로 올 방문 건수 집계, 오늘 포함 이후 등록완료 상태만
        long upcoming = visitorVehicleRepository
                .countByComplexIdAndVisitDateGreaterThanEqualAndStatusAndIsDeletedFalse(targetComplexId, today, VisitorVehicleStatus.APPROVED);

        // 이번 달 전체 건수 집계, 상태 무관 미삭제만
        long monthTotal = visitorVehicleRepository
                .countByComplexIdAndVisitDateBetweenAndIsDeletedFalse(targetComplexId, monthStart, monthEnd);

        return AdminVisitorVehicleStatsRes.builder()
                .todayScheduled(todayScheduled)
                .upcoming(upcoming)
                .monthTotal(monthTotal)
                .build();
    }

    // 요청자 userId로 활성 세대 매핑을 해석해 householdId를 반환한다. 미동기화면 세대 미존재로 처리.
    private Long resolveHouseholdId(Long userId) {
        return householdMemberCacheRepository.findByUserIdAndIsActiveTrue(userId)
                .map(HouseholdMemberCache::getHouseholdId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));
    }

}
