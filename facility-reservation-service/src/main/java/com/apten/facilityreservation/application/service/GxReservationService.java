package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxReservationRejectRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import com.apten.facilityreservation.domain.enums.GxReservationCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.repository.GxProgramRepository;
import com.apten.facilityreservation.domain.repository.GxReservationRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.time.LocalDateTime;
import java.util.List;
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
    public GxReservationApproveRes approveGxReservation(Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) gxReservationId가 현재 complexId 소속인지 검증한다.
        // 3) WAITING 상태와 정원 초과 여부 검증은 2단계에서 구현한다.
        // 4) 승인 처리와 approvedAt 저장은 2단계에서 구현한다.
        return GxReservationApproveRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.CONFIRMED)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    // GX 예약을 거절한다.
    public GxReservationRejectRes rejectGxReservation(Long complexId, Long gxReservationId, GxReservationRejectReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) gxReservationId가 현재 complexId 소속인지 검증한다.
        // 3) WAITING 상태 검증과 REJECTED 처리 로직은 2단계에서 구현한다.
        return GxReservationRejectRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.REJECTED)
                .rejectReason(req.getRejectReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
