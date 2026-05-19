package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.AdminGxReservationDetailRes;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxReservationRejectRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.application.model.response.MyGxReservationListRes;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import com.apten.facilityreservation.domain.enums.GxReservationCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.GxProgramRepository;
import com.apten.facilityreservation.domain.repository.GxReservationRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// GX 예약 신청과 승인, 거절 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class GxReservationService {

    private final FeatureAccessService featureAccessService;
    private final GxProgramRepository gxProgramRepository;
    private final GxReservationRepository gxReservationRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;
    private final FacilityRepository facilityRepository;

    // 내 GX 예약 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<MyGxReservationListRes> getMyGxReservations(Long userId, Long complexId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        List<GxReservation> reservations = gxReservationRepository.findByUserIdAndComplexId(userId, complexId);

        if (reservations.isEmpty()) {
            return List.of();
        }

        List<Long> programIds = reservations.stream()
                .map(GxReservation::getProgramId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, GxProgram> programMap = programIds.isEmpty()
                ? Map.of()
                : gxProgramRepository.findAllById(programIds)
                        .stream()
                        .collect(Collectors.toMap(GxProgram::getId, p -> p, (e1, e2) -> e1));

        return reservations.stream()
                .map(r -> {
                    GxProgram p = r.getProgramId() != null ? programMap.get(r.getProgramId()) : null;
                    return MyGxReservationListRes.builder()
                            .gxReservationId(r.getId())
                            .programId(r.getProgramId())
                            .programName(p != null ? p.getName() : null)
                            .startDate(p != null ? p.getStartDate() : null)
                            .endDate(p != null ? p.getEndDate() : null)
                            .startTime(p != null ? p.getStartTime() : null)
                            .endTime(p != null ? p.getEndTime() : null)
                            .daysOfWeek(p != null ? p.getDaysOfWeek() : null)
                            .baseFee(p != null ? p.getBaseFee() : null)
                            .status(r.getStatus())
                            .waitNo(r.getWaitNo())
                            .programStatus(p != null ? p.getStatus() : null)
                            .build();
                })
                .toList();
    }

    // GX 예약을 신청한다.
    @Transactional
    public GxReservationPostRes createGxReservation(Long userId, Long complexId, GxReservationPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = gxProgramRepository.findByIdAndComplexId(req.getProgramId(), complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

        // OPEN 상태 프로그램만 신청 가능
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }
        if (program.getStatus() != GxProgramStatus.OPEN) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 중복 신청 차단
        if (gxReservationRepository.existsByProgramIdAndUserId(req.getProgramId(), userId)) {
            throw new BusinessException(FacilityReservationErrorCode.GX_ALREADY_APPLIED);
        }

        // householdId 조회 — Kafka 기반 캐시 테이블에서 ACTIVE 세대원 기준으로 추출
        HouseholdMemberCache memberCache = householdMemberCacheRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        long confirmedCount = gxReservationRepository.countByProgramIdAndStatus(req.getProgramId(), GxReservationStatus.CONFIRMED);

        GxReservationStatus status;
        Integer waitNo = null;

        if (confirmedCount < program.getMaxCount()) {
            status = GxReservationStatus.CONFIRMED;
        } else if (Boolean.TRUE.equals(program.getWaitingEnabled())) {
            long currentWaiting = gxReservationRepository.countByProgramIdAndStatus(req.getProgramId(), GxReservationStatus.WAITING);
            status = GxReservationStatus.WAITING;
            waitNo = (int) (currentWaiting + 1);
        } else {
            throw new BusinessException(FacilityReservationErrorCode.GX_CAPACITY_FULL);
        }

        GxReservation reservation = gxReservationRepository.save(GxReservation.builder()
                .complexId(complexId)
                .programId(req.getProgramId())
                .userId(userId)
                .householdId(memberCache.getHouseholdId())
                .status(status)
                .waitNo(waitNo)
                .build());

        return GxReservationPostRes.builder()
                .gxReservationId(reservation.getId())
                .programId(reservation.getProgramId())
                .status(reservation.getStatus())
                .waitNo(reservation.getWaitNo())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // GX 대기 순번을 조회한다.
    @Transactional(readOnly = true)
    public GxWaitingRes getWaiting(Long userId, Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndUserIdAndComplexId(gxReservationId, userId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        return GxWaitingRes.builder()
                .gxReservationId(reservation.getId())
                .programId(reservation.getProgramId())
                .waitNo(reservation.getWaitNo())
                .status(reservation.getStatus())
                .build();
    }

    // GX 예약을 취소한다.
    @Transactional
    public GxReservationCancelRes cancelGxReservation(Long userId, Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndUserIdAndComplexId(gxReservationId, userId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // CONFIRMED 또는 WAITING 상태만 취소 가능
        if (reservation.getStatus() != GxReservationStatus.CONFIRMED
                && reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        boolean wasConfirmed = reservation.getStatus() == GxReservationStatus.CONFIRMED;
        reservation.cancel(GxReservationCancelReason.USER);

        // CONFIRMED 취소 시 첫 번째 WAITING 예약을 자동 승격
        if (wasConfirmed) {
            List<GxReservation> waitingList = gxReservationRepository
                    .findByProgramIdAndStatusOrderByWaitNoAsc(reservation.getProgramId(), GxReservationStatus.WAITING);
            if (!waitingList.isEmpty()) {
                waitingList.get(0).approve();
                // TODO: 대기자 승격 알림 발행 (가은 담당)
            }
        }

        return GxReservationCancelRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }

    // GX 예약을 승인한다.
    @Transactional
    public GxReservationApproveRes approveGxReservation(Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // WAITING 상태만 승인 가능
        if (reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 프로그램 정원 초과 여부 확인
        GxProgram program = gxProgramRepository.findByIdAndComplexId(reservation.getProgramId(), complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

        long confirmedCount = gxReservationRepository.countByProgramIdAndStatus(reservation.getProgramId(), GxReservationStatus.CONFIRMED);
        if (confirmedCount >= program.getMaxCount()) {
            throw new BusinessException(FacilityReservationErrorCode.GX_CAPACITY_FULL);
        }

        reservation.approve();

        // TODO: 승인 알림 발행 (가은 담당)

        return GxReservationApproveRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .approvedAt(reservation.getApprovedAt())
                .build();
    }

    // GX 예약을 거절한다.
    @Transactional
    public GxReservationRejectRes rejectGxReservation(Long complexId, Long gxReservationId, GxReservationRejectReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // WAITING 상태만 거절 가능
        if (reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        reservation.reject(req.getRejectReason());

        // TODO: 거절 알림 발행 (가은 담당)

        return GxReservationRejectRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .rejectReason(reservation.getRejectReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 GX 예약 단건 상세를 조회한다.
    @Transactional(readOnly = true)
    public AdminGxReservationDetailRes getAdminGxReservationDetail(Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        GxProgram program = reservation.getProgramId() != null
                ? gxProgramRepository.findById(reservation.getProgramId()).orElse(null)
                : null;

        Facility facility = (program != null && program.getFacilityId() != null)
                ? facilityRepository.findById(program.getFacilityId()).orElse(null)
                : null;

        // 캐시 데이터가 없으면 null로 내려가도 된다
        UserCache userCache = reservation.getUserId() != null
                ? userCacheRepository.findById(reservation.getUserId()).orElse(null)
                : null;
        HouseholdCache householdCache = reservation.getHouseholdId() != null
                ? householdCacheRepository.findByHouseholdId(reservation.getHouseholdId()).orElse(null)
                : null;

        String buildingNo = householdCache != null ? householdCache.getBuildingNo() : null;
        String unitNo = householdCache != null ? householdCache.getUnitNo() : null;
        String unit = (buildingNo != null && unitNo != null) ? buildingNo + "동 " + unitNo + "호" : null;

        return AdminGxReservationDetailRes.builder()
                .gxReservationId(reservation.getId())
                .programId(reservation.getProgramId())
                .programName(program != null ? program.getName() : null)
                .facilityId(program != null ? program.getFacilityId() : null)
                .facilityName(facility != null ? facility.getName() : null)
                .userId(reservation.getUserId())
                .householdId(reservation.getHouseholdId())
                .residentName(userCache != null ? userCache.getName() : null)
                .dong(buildingNo)
                .ho(unitNo)
                .unit(unit)
                .status(reservation.getStatus())
                .statusName(reservation.getStatus().getValue())
                .waitNo(reservation.getWaitNo())
                .startDate(program != null ? program.getStartDate() : null)
                .endDate(program != null ? program.getEndDate() : null)
                .startTime(program != null ? program.getStartTime() : null)
                .endTime(program != null ? program.getEndTime() : null)
                .baseFee(program != null ? program.getBaseFee() : null)
                .approvedAt(reservation.getApprovedAt())
                .rejectReason(reservation.getRejectReason())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // 관리자가 GX 예약을 강제 취소한다.
    @Transactional
    public GxReservationCancelRes cancelGxReservationByAdmin(Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // CONFIRMED 또는 WAITING 상태만 취소 가능
        if (reservation.getStatus() != GxReservationStatus.CONFIRMED
                && reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        boolean wasConfirmed = reservation.getStatus() == GxReservationStatus.CONFIRMED;
        reservation.cancel(GxReservationCancelReason.ADMIN);

        // CONFIRMED 취소 시 첫 번째 WAITING 예약을 자동 승격
        if (wasConfirmed) {
            List<GxReservation> waitingList = gxReservationRepository
                    .findByProgramIdAndStatusOrderByWaitNoAsc(reservation.getProgramId(), GxReservationStatus.WAITING);
            if (!waitingList.isEmpty()) {
                waitingList.get(0).approve();
            }
        }

        return GxReservationCancelRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }
}
