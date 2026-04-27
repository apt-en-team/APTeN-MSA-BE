package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxReservationRejectRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// GX 예약 신청과 승인, 거절 API 시그니처를 담당하는 서비스이다.
@Service
public class GxReservationService {

    // GX 예약을 신청한다.
    public GxReservationPostRes createGxReservation(GxReservationPostReq req) {
        //TODO gx_program 상태 OPEN 확인
        //TODO 중복 신청 여부 확인
        //TODO 다음 waitNo 계산
        //TODO WAITING 상태로 저장
        return GxReservationPostRes.builder()
                .gxReservationId(0L)
                .programId(req.getProgramId())
                .status(GxReservationStatus.WAITING)
                .waitNo(1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // GX 대기 순번을 조회한다.
    public GxWaitingRes getWaiting(Long gxReservationId) {
        //TODO gxReservationId 기준 대기 순번 조회
        return GxWaitingRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.WAITING)
                .build();
    }

    // GX 예약을 취소한다.
    public GxReservationCancelRes cancelGxReservation(Long gxReservationId) {
        //TODO GX 예약 소유자 검증
        //TODO WAITING 또는 CONFIRMED 상태 취소 처리
        return GxReservationCancelRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // GX 예약을 승인한다.
    public GxReservationApproveRes approveGxReservation(Long gxReservationId) {
        //TODO WAITING 상태인지 확인
        //TODO 현재 CONFIRMED 인원 조회
        //TODO maxCount 초과 여부 검증
        //TODO CONFIRMED 상태로 변경
        //TODO approvedAt 저장
        return GxReservationApproveRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.CONFIRMED)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    // GX 예약을 거절한다.
    public GxReservationRejectRes rejectGxReservation(Long gxReservationId, GxReservationRejectReq req) {
        //TODO WAITING 상태인지 확인
        //TODO REJECTED 상태와 거절 사유 저장
        return GxReservationRejectRes.builder()
                .gxReservationId(gxReservationId)
                .status(GxReservationStatus.REJECTED)
                .rejectReason(req.getReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
