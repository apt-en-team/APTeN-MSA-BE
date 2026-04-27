package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.AdminReservationCancelReq;
import com.apten.facilityreservation.application.model.request.AdminReservationListReq;
import com.apten.facilityreservation.application.model.request.AvailableTimeListReq;
import com.apten.facilityreservation.application.model.request.MyReservationListReq;
import com.apten.facilityreservation.application.model.request.ReservationCancelReq;
import com.apten.facilityreservation.application.model.request.ReservationPostReq;
import com.apten.facilityreservation.application.model.request.SeatHoldPostReq;
import com.apten.facilityreservation.application.model.response.AdminReservationCancelRes;
import com.apten.facilityreservation.application.model.response.AdminReservationDetailRes;
import com.apten.facilityreservation.application.model.response.AdminReservationListRes;
import com.apten.facilityreservation.application.model.response.AvailableTimeListRes;
import com.apten.facilityreservation.application.model.response.MyReservationDetailRes;
import com.apten.facilityreservation.application.model.response.MyReservationListRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ReservationCancelRes;
import com.apten.facilityreservation.application.model.response.ReservationCompleteRes;
import com.apten.facilityreservation.application.model.response.ReservationPostRes;
import com.apten.facilityreservation.application.model.response.TempHoldExpireRes;
import com.apten.facilityreservation.application.model.response.SeatHoldPostRes;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 일반 시설 예약과 좌석 선점 관련 API 시그니처를 담당하는 서비스이다.
@Service
public class ReservationService {

    // 예약 가능 시간 목록을 조회한다.
    public List<AvailableTimeListRes> getAvailableTimeList(AvailableTimeListReq req) {
        //TODO 시설 활성 상태 확인
        //TODO 운영 시간과 slotMin 기준 시간대 생성
        //TODO facility_block_time 차단 시간 제외
        //TODO CONFIRMED 예약 제외
        //TODO HOLDING 상태 TEMP_HOLD 제외
        //TODO SEAT/COUNT 방식별 잔여 좌석 또는 잔여 정원 계산
        return List.of();
    }

    // 좌석 임시 선점을 생성한다.
    public SeatHoldPostRes createSeatHold(SeatHoldPostReq req) {
        //TODO 좌석형 시설인지 확인
        //TODO 좌석 활성 상태 확인
        //TODO 예약 가능 시간 여부 확인
        //TODO 기존 CONFIRMED 예약 존재 여부 확인
        //TODO 기존 HOLDING TEMP_HOLD 존재 여부 확인
        //TODO reservation_temp_hold를 HOLDING 상태로 저장
        //TODO expiresAt을 현재 시각 + 15분으로 설정
        //TODO UNIQUE 제약 충돌 시 SEAT_TEMP_HOLD_EXISTS 반환
        return SeatHoldPostRes.builder()
                .holdId(0L)
                .facilityId(req.getFacilityId())
                .seatId(req.getSeatId())
                .reservationDate(req.getReservationDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(ReservationHoldStatus.HOLDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
    }

    // 만료된 좌석 임시 선점을 자동 해제한다.
    public TempHoldExpireRes expireSeatHolds() {
        //TODO expiresAt이 지난 HOLDING 선점 조회
        //TODO holdStatus를 EXPIRED로 변경
        //TODO 만료 처리 건수 반환
        return TempHoldExpireRes.builder()
                .expiredCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // 예약 생성을 처리한다.
    public ReservationPostRes createReservation(ReservationPostReq req) {
        //TODO facility 활성 상태 확인
        //TODO 운영 시간/차단 시간/예약 단위 검증
        //TODO 동일 사용자 동일 시간 중복 예약 검증
        //TODO 좌석형이면 유효한 HOLDING tempHold 확인
        //TODO 좌석형이면 tempHold를 CONFIRMED로 변경
        //TODO COUNT형이면 maxCount 기준 잔여 정원 비관적 락 또는 트랜잭션 검증
        //TODO reservation을 CONFIRMED로 저장
        //TODO 필요 시 예약 상태 변경 이벤트 outbox 적재
        return ReservationPostRes.builder()
                .reservationId(0L)
                .facilityId(req.getFacilityId())
                .reservationDate(req.getReservationDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 내 예약 목록을 조회한다.
    public PageResponse<MyReservationListRes> getMyReservationList(MyReservationListReq req) {
        //TODO 로그인 사용자 기준 내 예약 목록 조회
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 내 예약 상세를 조회한다.
    public MyReservationDetailRes getMyReservationDetail(Long reservationId) {
        //TODO 예약 소유자 검증
        //TODO 예약 상세 조회
        return MyReservationDetailRes.builder().reservationId(reservationId).build();
    }

    // 내 예약을 취소한다.
    public ReservationCancelRes cancelReservation(Long reservationId, ReservationCancelReq req) {
        //TODO 예약 소유자 검증
        //TODO cancelDeadlineHours 기준 취소 가능 시간 검증
        //TODO reservation 상태를 CANCELLED로 변경
        //TODO cancelReason, cancelledAt 저장
        //TODO 예약 취소 알림 이벤트 outbox 적재
        return ReservationCancelRes.builder()
                .reservationId(reservationId)
                .status(ReservationStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 관리자 예약 목록을 조회한다.
    public PageResponse<AdminReservationListRes> getAdminReservationList(AdminReservationListReq req) {
        //TODO complexId 기준 관리자 예약 목록 조회
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 관리자 예약 상세를 조회한다.
    public AdminReservationDetailRes getAdminReservationDetail(Long reservationId) {
        //TODO reservationId 기준 관리자 예약 상세 조회
        return AdminReservationDetailRes.builder().reservationId(reservationId).build();
    }

    // 관리자가 예약을 강제 취소한다.
    public AdminReservationCancelRes cancelReservationByAdmin(Long reservationId, AdminReservationCancelReq req) {
        //TODO 예약 존재 여부 확인
        //TODO reservation 상태를 CANCELLED로 변경
        //TODO cancelReason, cancelledAt 저장
        //TODO 예약 취소 알림 이벤트 outbox 적재
        return AdminReservationCancelRes.builder()
                .reservationId(reservationId)
                .status(ReservationStatus.CANCELLED)
                .reason(req.getReason())
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 시간이 지난 예약을 완료 처리한다.
    public ReservationCompleteRes completeReservations() {
        //TODO endTime이 지난 CONFIRMED 예약 조회
        //TODO COMPLETED 상태로 변경
        //TODO completedAt 저장
        //TODO 시설 이용 비용 산정 대상이 되도록 처리
        return ReservationCompleteRes.builder()
                .completedCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
