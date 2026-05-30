package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehiclePatchReq;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.support.RoleContextValidator;
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.RegularVisitorVehicle;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.RegularVisitorVehicleRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import com.apten.parkingvehicle.infrastructure.kafka.ParkingVehicleOutboxService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 고정 방문차량 등록과 삭제 흐름을 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class RegularVisitorVehicleService {

    // 고정 방문차량 원본 저장소
    private final RegularVisitorVehicleRepository regularVisitorVehicleRepository;

    // 세대 캐시 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // 고정 방문차량 알림 outbox 적재 서비스
    private final ParkingVehicleOutboxService parkingVehicleOutboxService;

    // 고정 방문차량을 등록한다.
    @Transactional
    public RegularVisitorVehicleCreateRes createRegularVisitorVehicle(
            RegularVisitorVehicleCreateReq request, Long userId, String userRole, Long complexId
    ) {
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

        // 시작일 필수값 검증, 종료일은 미설정 허용
        LocalDate startDate = request.getStartDate();
        if (startDate == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기간 일관성 검증, endDate가 있을 때 startDate 이전이면 거부
        LocalDate endDate = request.getEndDate();
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 세대주 기준 세대 조회, 세대원이면 여기서 차단
        HouseholdCache household = householdCacheRepository.findByHeadUserId(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 헤더 단지와 세대 단지 일치 검증, 불일치는 세대 미존재와 동일하게 처리
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND);
        }

        // 신규 고정 방문차량 엔티티, isActive/isDeleted는 빌더 디폴트
        RegularVisitorVehicle entity = RegularVisitorVehicle.builder()
                .userId(userId)
                .householdId(household.getId())
                .complexId(complexId)
                .licensePlate(licensePlate)
                .visitorName(request.getVisitorName())
                .phone(request.getPhone())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        RegularVisitorVehicle saved = regularVisitorVehicleRepository.save(entity);

        // 고정 방문차량 등록 알림 outbox 적재 — 관리자 대상
        parkingVehicleOutboxService.saveRegularVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.REGULAR_VISITOR_VEHICLE_REGISTERED,
                ParkingVehicleOutboxService.TARGET_ADMIN,
                userId);

        return RegularVisitorVehicleCreateRes.builder()
                .regularVisitorVehicleId(saved.getId())
                .licensePlate(saved.getLicensePlate())
                .visitorName(saved.getVisitorName())
                .phone(saved.getPhone())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .isActive(saved.getIsActive())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 내 고정 방문차량 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<RegularVisitorVehicleListRes> getMyRegularVisitorVehicleList(
            RegularVisitorVehicleListReq request, Long userId, String userRole, Long complexId
    ) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 페이지 파라미터 디폴트 방어
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 활성 필터 유무로 쿼리 메서드 분기, Boolean null 비교 회피
        Page<RegularVisitorVehicle> resultPage = request.getIsActive() != null
                ? regularVisitorVehicleRepository.findByUserIdAndIsActiveAndIsDeletedFalse(userId, request.getIsActive(), pageable)
                : regularVisitorVehicleRepository.findByUserIdAndIsDeletedFalse(userId, pageable);

        // 응답 DTO 매핑
        List<RegularVisitorVehicleListRes> content = resultPage.getContent().stream()
                .map(r -> RegularVisitorVehicleListRes.builder()
                        .regularVisitorVehicleId(r.getId())
                        .licensePlate(r.getLicensePlate())
                        .visitorName(r.getVisitorName())
                        .phone(r.getPhone())
                        .startDate(r.getStartDate())
                        .endDate(r.getEndDate())
                        .isActive(r.getIsActive())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();

        return PageResponse.<RegularVisitorVehicleListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 고정 방문차량을 수정한다.
    @Transactional
    public RegularVisitorVehiclePatchRes updateRegularVisitorVehicle(
            Long regularVisitorVehicleId,
            RegularVisitorVehiclePatchReq request,
            Long userId, String userRole, Long complexId
    ) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 요청 본문 null 검증
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 고정 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        RegularVisitorVehicle entity = regularVisitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(regularVisitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.REGULAR_VISITOR_NOT_FOUND));

        // 고정 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!entity.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.REGULAR_VISITOR_COMPLEX_MISMATCH);
        }

        // 부분 갱신값 계산, null 필드는 기존값 유지
        String visitorName = request.getVisitorName() != null ? request.getVisitorName() : entity.getVisitorName();
        String phone = request.getPhone() != null ? request.getPhone() : entity.getPhone();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : entity.getStartDate();
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : entity.getEndDate();
        Boolean isActive = request.getIsActive() != null ? request.getIsActive() : entity.getIsActive();

        // 기간 일관성 검증, endDate가 있을 때 startDate 이전이면 거부
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 엔티티 부분 갱신 적용
        entity.update(visitorName, phone, startDate, endDate, isActive);

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        RegularVisitorVehicle saved = regularVisitorVehicleRepository.save(entity);

        // 고정 방문차량 수정 알림 outbox 적재 — 관리자 대상
        parkingVehicleOutboxService.saveRegularVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.REGULAR_VISITOR_VEHICLE_UPDATED,
                ParkingVehicleOutboxService.TARGET_ADMIN,
                userId);

        return RegularVisitorVehiclePatchRes.builder()
                .regularVisitorVehicleId(saved.getId())
                .visitorName(saved.getVisitorName())
                .phone(saved.getPhone())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .isActive(saved.getIsActive())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 고정 방문차량을 삭제한다.
    @Transactional
    public RegularVisitorVehicleDeleteRes deleteRegularVisitorVehicle(
            Long regularVisitorVehicleId,
            Long userId, String userRole, Long complexId
    ) {
        // 입주민 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // 고정 방문차량 단건 + 소유자 동시 검증, 미존재 시 404
        RegularVisitorVehicle entity = regularVisitorVehicleRepository.findByIdAndUserIdAndIsDeletedFalse(regularVisitorVehicleId, userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.REGULAR_VISITOR_NOT_FOUND));

        // 고정 방문차량 단지가 요청 단지와 다르면 단지 불일치로 접근 차단
        if (!entity.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.REGULAR_VISITOR_COMPLEX_MISMATCH);
        }

        // 소프트 삭제, isDeleted/deletedAt/isActive를 markDeleted가 한 번에 처리
        entity.markDeleted(LocalDateTime.now());

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        RegularVisitorVehicle saved = regularVisitorVehicleRepository.save(entity);

        return RegularVisitorVehicleDeleteRes.builder()
                .message("고정 방문차량 삭제 완료")
                .deletedAt(saved.getDeletedAt())
                .build();
    }

    // 관리자가 고정 방문차량을 강제 삭제한다.
    @Transactional
    public AdminRegularVisitorVehicleDeleteRes deleteRegularVisitorVehicleByAdmin(
            Long regularVisitorVehicleId,
            Long actorUserId,
            String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석으로 대상 단지 확정
        Long targetComplexId = RoleContextValidator.resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 고정 방문차량 단건 존재 확인
        RegularVisitorVehicle entity = regularVisitorVehicleRepository.findByIdAndIsDeletedFalse(regularVisitorVehicleId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.REGULAR_VISITOR_NOT_FOUND));

        // 고정 방문차량 단지가 관리자 컨텍스트 단지와 다르면 단지 불일치로 접근 차단
        if (!entity.getComplexId().equals(targetComplexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.REGULAR_VISITOR_COMPLEX_MISMATCH);
        }

        // 소프트 삭제, isDeleted/deletedAt/isActive를 markDeleted가 한 번에 처리
        entity.markDeleted(LocalDateTime.now());

        // dirty checking 명시화, 다른 메서드와 패턴 통일
        RegularVisitorVehicle saved = regularVisitorVehicleRepository.save(entity);

        // 관리자 강제 삭제 알림 outbox 적재 — 세대 대상, actorUserId는 컨트롤러가 전달한 관리자 식별자
        parkingVehicleOutboxService.saveRegularVisitorVehicleNotificationEvent(
                saved,
                ParkingVehicleOutboxService.REGULAR_VISITOR_VEHICLE_FORCE_DELETED_BY_ADMIN,
                ParkingVehicleOutboxService.TARGET_HOUSEHOLD,
                actorUserId);

        return AdminRegularVisitorVehicleDeleteRes.builder()
                .message("고정 방문차량 강제 삭제 완료")
                .deletedAt(saved.getDeletedAt())
                .build();
    }

}
