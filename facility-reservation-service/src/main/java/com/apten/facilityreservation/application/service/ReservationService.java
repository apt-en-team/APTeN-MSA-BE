package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.AdminReservationCancelReq;
import com.apten.facilityreservation.application.model.request.AdminReservationSearchReq;
import com.apten.facilityreservation.application.model.request.FacilityStatusSearchReq;
import com.apten.facilityreservation.application.model.request.GolfStatusSearchReq;
import com.apten.facilityreservation.application.model.request.GymStatusSearchReq;
import com.apten.facilityreservation.application.model.request.MyReservationSearchReq;
import com.apten.facilityreservation.application.model.request.ReservationAvailableTimeSearchReq;
import com.apten.facilityreservation.application.model.request.ReservationPostReq;
import com.apten.facilityreservation.application.model.request.ReservationSeatHoldPostReq;
import com.apten.facilityreservation.application.model.request.ReservationSeatSearchReq;
import com.apten.facilityreservation.application.model.request.StudyRoomStatusSearchReq;
import com.apten.facilityreservation.application.model.response.AdminReservationCancelRes;
import com.apten.facilityreservation.application.model.response.AdminReservationGetPageRes;
import com.apten.facilityreservation.application.model.response.FacilityStatusRes;
import com.apten.facilityreservation.application.model.response.GolfStatusRes;
import com.apten.facilityreservation.application.model.response.GymStatusRes;
import com.apten.facilityreservation.application.model.response.MyReservationGetPageRes;
import com.apten.facilityreservation.application.model.response.ReservationAvailableTimeRes;
import com.apten.facilityreservation.application.model.response.ReservationCancelRes;
import com.apten.facilityreservation.application.model.response.ReservationGetDetailRes;
import com.apten.facilityreservation.application.model.response.ReservationPostRes;
import com.apten.facilityreservation.application.model.response.ReservationSeatHoldDeleteRes;
import com.apten.facilityreservation.application.model.response.ReservationSeatHoldPostRes;
import com.apten.facilityreservation.application.model.response.ReservationSeatRes;
import com.apten.facilityreservation.application.model.response.StudyRoomSeatStatusRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 일반 예약 응용 서비스
// 좌석 선점과 예약 생성, 내 예약과 관리자 예약 현황 시그니처를 이 서비스에 모아둔다
@Service
public class ReservationService {

    public ReservationPostRes createReservation(ReservationPostReq request) {
        // TODO: 일반 예약 생성 로직 구현
        // TODO: facility와 facility_type, facility_policy를 함께 조회해 실제 예약 정책 값을 계산한다.
        // TODO: facility.slotMin과 facility.baseFee가 있으면 시설 override를 우선 적용한다.
        // TODO: 정원 초과 동시성 제어 처리
        // TODO: 예약 상태 변경 이벤트 발행
        return ReservationPostRes.builder()
                .facilityUid(request.getFacilityUid())
                .reservationDate(request.getReservationDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .seatNo(request.getSeatNo())
                .quantity(request.getQuantity())
                .status("CONFIRMED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<ReservationAvailableTimeRes> getAvailableTimeList(ReservationAvailableTimeSearchReq request) {
        // TODO: 예약 가능 시간 조회 로직 구현
        return List.of();
    }

    public List<ReservationSeatRes> getSeatStatusList(ReservationSeatSearchReq request) {
        // TODO: 좌석 상태 조회 로직 구현
        return List.of();
    }

    public ReservationSeatHoldPostRes createSeatHold(ReservationSeatHoldPostReq request) {
        // TODO: 좌석 임시선점 로직 구현
        // TODO: 좌석 임시선점 만료 스케줄러 처리
        return ReservationSeatHoldPostRes.builder()
                .facilityUid(request.getFacilityUid())
                .seatNo(request.getSeatNo())
                .status("TEMP_HOLD")
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
    }

    public ReservationSeatHoldDeleteRes releaseSeatHold(String holdToken) {
        // TODO: 좌석 임시선점 해제 로직 구현
        return ReservationSeatHoldDeleteRes.builder()
                .message("좌석 임시선점 해제 완료")
                .releasedAt(LocalDateTime.now())
                .build();
    }

    public MyReservationGetPageRes getMyReservationList(MyReservationSearchReq request) {
        // TODO: 내 예약 목록 조회 로직 구현
        return MyReservationGetPageRes.empty(request.getPage(), request.getSize());
    }

    public ReservationGetDetailRes getReservationDetail(String reservationUid) {
        // TODO: 예약 상세 조회 로직 구현
        return ReservationGetDetailRes.builder().build();
    }

    public ReservationCancelRes cancelReservation(String reservationUid) {
        // TODO: 예약 취소 가능 시간 검증 후 취소 처리
        // TODO: 취소 가능 시간은 facility_policy.cancel_deadline_hours를 기준으로 계산한다.
        // TODO: 예약 상태 변경 이벤트 발행
        return ReservationCancelRes.builder()
                .reservationUid(reservationUid)
                .status("CANCELLED")
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    public AdminReservationGetPageRes getAdminReservationList(AdminReservationSearchReq request) {
        // TODO: 관리자 예약 목록 조회 로직 구현
        return AdminReservationGetPageRes.empty(request.getPage(), request.getSize());
    }

    public AdminReservationCancelRes cancelReservationByAdmin(String reservationUid, AdminReservationCancelReq request) {
        // TODO: 관리자 예약 강제 취소 로직 구현
        // TODO: 예약 상태 변경 이벤트 발행
        return AdminReservationCancelRes.builder()
                .reservationUid(reservationUid)
                .status("CANCELLED")
                .cancelledAt(LocalDateTime.now())
                .reason(request.getReason())
                .build();
    }

    public FacilityStatusRes getFacilityStatus(FacilityStatusSearchReq request) {
        // TODO: 시설별 예약 현황 조회 로직 구현
        return FacilityStatusRes.builder().facilityTypeUid(request.getFacilityTypeUid()).targetDate(request.getTargetDate()).items(List.of()).build();
    }

    public StudyRoomSeatStatusRes getStudyRoomStatus(StudyRoomStatusSearchReq request) {
        // TODO: 독서실 좌석 현황 조회 로직 구현
        return StudyRoomSeatStatusRes.builder().targetDate(request.getTargetDate()).startTime(request.getStartTime()).seats(List.of()).build();
    }

    public GolfStatusRes getGolfStatus(GolfStatusSearchReq request) {
        // TODO: 골프 좌석 현황 조회 로직 구현
        return GolfStatusRes.builder().targetDate(request.getTargetDate()).slots(List.of()).build();
    }

    public GymStatusRes getGymStatus(GymStatusSearchReq request) {
        // TODO: 헬스장 이용 현황 조회 로직 구현
        return GymStatusRes.builder().targetDate(request.getTargetDate()).reservedCount(0).users(List.of()).build();
    }

    // TODO: 이용 종료 예약 COMPLETED 처리
}
