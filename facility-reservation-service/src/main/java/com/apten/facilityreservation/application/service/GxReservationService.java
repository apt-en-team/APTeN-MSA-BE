package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.AdminGxReservationDetailRes;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.ReservationCompleteRes;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// GX 예약 신청/승인/거절 관리
@Service
@RequiredArgsConstructor
public class GxReservationService {

    // 완료 처리 배치 크기
    @Value("${apten.scheduler.gx-complete.batch-size:100}")
    private int gxCompleteBatchSize;

    private final FeatureAccessService featureAccessService;
    private final GxProgramRepository gxProgramRepository;
    private final GxReservationRepository gxReservationRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityNotificationService facilityNotificationService;

    // 내 GX 예약 목록 조회
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

    // GX 예약 신청
    @Transactional
    public GxReservationPostRes createGxReservation(Long userId, Long complexId, GxReservationPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        // 대기 순번 충돌 방지 (비관적 락)
        GxProgram program = gxProgramRepository.findByIdAndComplexIdForUpdate(req.getProgramId(), complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

        // OPEN 프로그램 신청 허용
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }
        if (program.getStatus() == GxProgramStatus.CLOSED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_RECRUITING_CLOSED);
        }
        if (program.getStatus() != GxProgramStatus.OPEN) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 활성 신청 중복 방지 (취소/거절/완료 후 재신청 허용)
        List<GxReservationStatus> activeStatuses = List.of(GxReservationStatus.WAITING, GxReservationStatus.CONFIRMED);
        if (gxReservationRepository.existsByProgramIdAndUserIdAndStatusIn(req.getProgramId(), userId, activeStatuses)) {
            throw new BusinessException(FacilityReservationErrorCode.GX_ALREADY_APPLIED);
        }

        // 세대 ID 조회 (Kafka 캐시, ACTIVE 세대원)
        HouseholdMemberCache memberCache = householdMemberCacheRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        // 전원 WAITING 접수
        long currentWaiting = gxReservationRepository.countByProgramIdAndStatus(req.getProgramId(), GxReservationStatus.WAITING);
        int waitNo = (int) (currentWaiting + 1);

        // 재신청 기존 row 재활성화
        GxReservation reservation = gxReservationRepository
                .findByProgramIdAndUserId(req.getProgramId(), userId)
                .map(existing -> {
                    existing.reapply(waitNo);
                    return existing;
                })
                .orElseGet(() -> gxReservationRepository.save(GxReservation.builder()
                        .complexId(complexId)
                        .programId(req.getProgramId())
                        .userId(userId)
                        .householdId(memberCache.getHouseholdId())
                        .status(GxReservationStatus.WAITING)
                        .waitNo(waitNo)
                        .build()));

        // GX 신청 알림 예약 (afterCommit, best-effort)
        facilityNotificationService.notifyGxApplied(userId, complexId, reservation, program);

        // 최소 인원 도달 알림 (최초 1회)
        // 최소 인원 미설정 제외
        if (program.getMinCount() > 0 && waitNo == program.getMinCount()) {
            facilityNotificationService.notifyGxMinimumReached(complexId, program);
        }

        return GxReservationPostRes.builder()
                .gxReservationId(reservation.getId())
                .programId(reservation.getProgramId())
                .status(reservation.getStatus())
                .waitNo(reservation.getWaitNo())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // GX 대기 순번 조회
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

    // GX 예약 취소
    @Transactional
    public GxReservationCancelRes cancelGxReservation(Long userId, Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndUserIdAndComplexId(gxReservationId, userId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // 활성 신청 취소 허용
        if (reservation.getStatus() != GxReservationStatus.CONFIRMED
                && reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        reservation.cancel(GxReservationCancelReason.USER);

        // 대기 순번 재정렬 (자동 승격 제외)
        resequenceWaitingNos(reservation.getProgramId());

        return GxReservationCancelRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }

    // GX 예약 승인
    @Transactional
    public GxReservationApproveRes approveGxReservation(Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // WAITING 신청 승인 허용
        if (reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 관리자 동시 승인 방지 (낙관적 락)
        try {
            GxProgram program = gxProgramRepository
                    .findByIdAndComplexIdWithOptimisticLock(reservation.getProgramId(), complexId)
                    .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

            long confirmedCount = gxReservationRepository.countByProgramIdAndStatus(reservation.getProgramId(), GxReservationStatus.CONFIRMED);
            if (confirmedCount >= program.getMaxCount()) {
                throw new BusinessException(FacilityReservationErrorCode.GX_CAPACITY_FULL);
            }

            reservation.approve();

            // GX 승인 알림 예약 (afterCommit, best-effort)
            facilityNotificationService.notifyGxApproved(reservation.getUserId(), complexId, reservation, program);

            // 대기 순번 재정렬
            resequenceWaitingNos(reservation.getProgramId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(FacilityReservationErrorCode.GX_CONCURRENT_UPDATE);
        }

        return GxReservationApproveRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .approvedAt(reservation.getApprovedAt())
                .build();
    }

    // GX 예약 거절
    @Transactional
    public GxReservationRejectRes rejectGxReservation(Long complexId, Long gxReservationId, GxReservationRejectReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // WAITING 신청 거절 허용
        if (reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        try {
            GxProgram program = gxProgramRepository
                    .findByIdAndComplexIdWithOptimisticLock(reservation.getProgramId(), complexId)
                    .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

            reservation.reject(req.getRejectReason());

            // GX 거절 알림 예약 (afterCommit, best-effort)
            facilityNotificationService.notifyGxRejected(reservation.getUserId(), complexId, reservation, program);

            // 대기 순번 재정렬
            resequenceWaitingNos(reservation.getProgramId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(FacilityReservationErrorCode.GX_CONCURRENT_UPDATE);
        }

        return GxReservationRejectRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .rejectReason(reservation.getRejectReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 GX 예약 상세 조회
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

        // 누락 캐시 null 허용
        UserCache userCache = reservation.getUserId() != null
                ? userCacheRepository.findById(reservation.getUserId()).orElse(null)
                : null;
        HouseholdCache householdCache = reservation.getHouseholdId() != null
                ? householdCacheRepository.findByHouseholdId(reservation.getHouseholdId()).orElse(null)
                : null;

        String buildingNo = householdCache != null ? householdCache.getBuildingNo() : null;
        String unitNo = householdCache != null ? householdCache.getUnitNo() : null;
        String unit = (buildingNo != null && unitNo != null) ? buildingNo + "동 " + unitNo + "호" : null;

        long confirmedCount = reservation.getProgramId() != null
                ? gxReservationRepository.countByProgramIdAndStatus(
                        reservation.getProgramId(), GxReservationStatus.CONFIRMED)
                : 0L;

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
                .status(reservation.getStatus().name())
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
                .confirmedCount(confirmedCount)
                .maxCount(program != null ? program.getMaxCount() : null)
                .build();
    }

    // 관리자 GX 예약 강제 취소
    @Transactional
    public GxReservationCancelRes cancelGxReservationByAdmin(Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxReservation reservation = gxReservationRepository
                .findByIdAndComplexId(gxReservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_RESERVATION_NOT_FOUND));

        // 활성 신청 취소 허용
        if (reservation.getStatus() != GxReservationStatus.CONFIRMED
                && reservation.getStatus() != GxReservationStatus.WAITING) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        try {
            GxProgram program = gxProgramRepository
                    .findByIdAndComplexIdWithOptimisticLock(reservation.getProgramId(), complexId)
                    .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

            reservation.cancel(GxReservationCancelReason.ADMIN);

            // 관리자 취소 결과 알림 (GX 거절)
            facilityNotificationService.notifyGxRejected(reservation.getUserId(), complexId, reservation, program);

            // 대기 순번 재정렬 (자동 승격 제외)
            resequenceWaitingNos(reservation.getProgramId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(FacilityReservationErrorCode.GX_CONCURRENT_UPDATE);
        }

        return GxReservationCancelRes.builder()
                .gxReservationId(reservation.getId())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }

    // 종료 GX 예약 완료 처리
    @Transactional
    public ReservationCompleteRes completeGxReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<GxReservationStatus> targetStatuses = List.of(
                GxReservationStatus.WAITING, GxReservationStatus.CONFIRMED
        );

        List<GxReservation> completable = gxReservationRepository.findCompletableGxReservations(
                targetStatuses,
                now.toLocalDate(),
                now.toLocalTime(),
                PageRequest.of(0, Math.max(gxCompleteBatchSize, 1))
        );

        completable.forEach(GxReservation::complete);

        return ReservationCompleteRes.builder()
                .completedCount(completable.size())
                .processedAt(now)
                .build();
    }

    // 대기 순번 재정렬
    void resequenceWaitingNos(Long programId) {
        List<GxReservation> waitingList = gxReservationRepository
                .findByProgramIdAndStatusOrderByWaitNoAsc(programId, GxReservationStatus.WAITING);
        for (int i = 0; i < waitingList.size(); i++) {
            waitingList.get(i).assignWaitNo(i + 1);
        }
    }
}
