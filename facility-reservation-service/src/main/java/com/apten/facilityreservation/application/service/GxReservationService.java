package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxReservationRejectRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// GX 예약 신청과 승인, 거절 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class GxReservationService {

    private final FeatureAccessService featureAccessService;

    // GX 예약을 신청한다.
    public GxReservationPostRes createGxReservation(Long userId, Long complexId, GxReservationPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) household_member_cache -> household_cache 순으로 householdId와 단지 일치 여부를 검증한다.
        // 4) gx_reservation.householdId에 세대 ID를 저장한다.
        // 5) 중복 신청과 WAITING 순번 계산은 2단계에서 구현한다.
        return GxReservationPostRes.builder()
                .gxReservationId(0L)
                .programId(req.getProgramId())
                .status(GxReservationStatus.WAITING)
                .waitNo(1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // GX 대기 순번을 조회한다.
    public GxWaitingRes getWaiting(Long userId, Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) gxReservationId가 userId 소유이며 complexId 소속인지 검증한다.
        // 3) 대기 순번 조회 로직은 2단계에서 구현한다.
        return GxWaitingRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.WAITING)
                .build();
    }

    // GX 예약을 취소한다.
    public GxReservationCancelRes cancelGxReservation(Long userId, Long complexId, Long gxReservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) gxReservationId가 userId 소유이며 complexId 소속인지 검증한다.
        // 3) WAITING 또는 CONFIRMED 상태인지 확인한다.
        // 4) USER 취소 처리와 후속 대기 승격 로직은 2단계에서 구현한다.
        return GxReservationCancelRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.CANCELLED)
                .cancelReason(com.apten.facilityreservation.domain.enums.GxReservationCancelReason.USER)
                .cancelledAt(LocalDateTime.now())
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
