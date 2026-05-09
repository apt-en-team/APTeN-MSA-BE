package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
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
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import com.apten.facilityreservation.infrastructure.redis.ReservationTempHoldRedisService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 일반 시설 예약과 좌석 선점 관련 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
    private final ReservationTempHoldRedisService reservationTempHoldRedisService;

    // 예약 가능 시간 목록을 조회한다.
    public List<AvailableTimeListRes> getAvailableTimeList(Long userId, Long complexId, AvailableTimeListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속이고 활성 상태인지 검증한다.
        // 3) 운영 시간과 slotMin 기준으로 시간대를 생성한다.
        // 4) facility_block_time 차단 시간대를 제외한다.
        // 5) CONFIRMED 예약과 HOLDING TEMP_HOLD를 함께 반영해 예약 가능 여부를 계산한다.
        // 6) Redis TEMP_HOLD 실시간 선점 확인은 2단계에서 구현한다.
        return List.of();
    }

    // 좌석 임시 선점을 생성한다.
    public SeatHoldPostRes createSeatHold(Long userId, Long complexId, SeatHoldPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) facility가 SEAT 예약 방식인지 검증한다.
        // 4) seatId가 facility에 속하고 활성 상태인지 검증한다.
        // 5) 운영 시간, 차단 시간, 예약 가능 시간 범위를 검증한다.
        // 6) reservationTempHoldRedisService.buildHoldKey(...)로 Redis key를 생성한다.
        // 7) reservationTempHoldRedisService.tryHoldSeat(...)로 setIfAbsent + TTL 선점을 시도한다.
        // 8) 이미 key가 있으면 SEAT_TEMP_HOLD_EXISTS 또는 TIME_SLOT_NOT_AVAILABLE 예외를 반환한다.
        // 9) 선점 성공 시 reservation_temp_hold에 HOLDING 상태 이력을 저장한다.
        // 10) holdId, expiresAt을 응답으로 반환한다.
        // 11) Redis value 설계와 TTL 분 단위 정책은 2단계에서 확정한다.
        return SeatHoldPostRes.builder()
                .holdId(0L)
                .facilityId(req.getFacilityId())
                .seatId(req.getSeatId())
                .holdStatus(ReservationHoldStatus.HOLDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
    }

    // 만료된 좌석 임시 선점을 자동 해제한다.
    public TempHoldExpireRes expireSeatHolds() {
        // TODO:
        // 1) holdStatus=HOLDING 이고 expiresAt이 지난 reservation_temp_hold 목록을 조회한다.
        // 2) reservationTempHoldRedisService.existsHold(...)로 Redis key 존재 여부를 확인한다.
        // 3) 만료된 데이터는 holdStatus를 EXPIRED로 변경한다.
        // 4) Redis TTL 만료와 DB 상태 정합성 보정 전략은 2단계에서 구현한다.
        // 5) expiredCount를 집계해 반환한다.
        return TempHoldExpireRes.builder()
                .expiredCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // 예약 생성을 처리한다.
    public ReservationPostRes createReservation(Long userId, Long complexId, ReservationPostReq req) {
        Facility facility = facilityRepository.findByIdAndIsDeletedFalse(req.getFacilityId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));
        featureAccessService.validateEnabled(facility.getComplexId(), FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) household_member_cache에서 userId 기준 ACTIVE 세대원 정보를 조회한다.
        // 3) household_cache의 complexId와 요청 complexId 일치 여부를 검증한다.
        // 4) facilityId가 현재 complexId 소속인지 검증한다.
        // 5) facility/seat/날짜/시간 유효성을 검증한다.
        // 6) 좌석형이면 holdId로 reservation_temp_hold를 조회하고 userId, facilityId, seatId, 날짜/시간 일치 여부를 검증한다.
        // 7) reservationTempHoldRedisService.validateHold(...)로 Redis key 유효성을 검증한다.
        // 8) 기존 CONFIRMED 예약 중복을 조회한다.
        // 9) COUNT형이면 정원 초과 여부를 계산한다.
        // 10) reservation 저장 후 reservation.householdId에 현재 세대 ID를 함께 저장한다.
        // 11) reservation_temp_hold 상태를 CONFIRMED로 변경한다.
        // 12) reservationTempHoldRedisService.releaseHold(...)로 Redis key 삭제 또는 확정 처리를 수행한다.
        // 13) 예약 상태 변경 outbox 적재는 2단계에서 구현한다.
        return ReservationPostRes.builder()
                .reservationId(0L)
                .facilityId(req.getFacilityId())
                .seatId(req.getSeatId())
                .reservationDate(req.getReservationDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 내 예약 목록을 조회한다.
    public PageResponse<MyReservationListRes> getMyReservationList(Long userId, Long complexId, MyReservationListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) userId와 complexId 기준으로 본인 예약만 조회한다.
        // 3) fromDate, toDate, status 필터를 적용한다.
        // 4) seatNo, createdAt 등 응답 필드를 함께 조합한다.
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 내 예약 상세를 조회한다.
    public MyReservationDetailRes getMyReservationDetail(Long userId, Long complexId, Long reservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) reservationId가 userId 소유이며 complexId 소속인지 검증한다.
        // 3) 취소 가능 여부와 seatNo, createdAt 정보를 함께 조합한다.
        return MyReservationDetailRes.builder().reservationId(reservationId).build();
    }

    // 내 예약을 취소한다.
    public ReservationCancelRes cancelReservation(Long userId, Long complexId, Long reservationId, ReservationCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) reservationId가 userId 소유이며 complexId 소속인지 검증한다.
        // 3) facility_policy.cancelDeadlineHours 기준으로 취소 가능 여부를 검증한다.
        // 4) reservation 상태를 CANCELLED로 변경하고 USER 취소 사유를 저장한다.
        // 5) 예약 취소 알림 이벤트 outbox 적재를 수행한다.
        return ReservationCancelRes.builder()
                .reservationId(reservationId)
                .status(ReservationStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 관리자 예약 목록을 조회한다.
    public PageResponse<AdminReservationListRes> getAdminReservationList(Long complexId, AdminReservationListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) 관리자 단지 컨텍스트 complexId 기준 예약만 조회한다.
        // 3) facilityId, reservationDate, status, page, size 필터를 적용한다.
        // 4) residentName, seatNo, createdAt 정보를 함께 조합한다.
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 관리자 예약 상세를 조회한다.
    public AdminReservationDetailRes getAdminReservationDetail(Long complexId, Long reservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) reservationId가 현재 complexId 소속인지 검증한다.
        // 3) residentName, facilityName, cancel/complete timestamps를 함께 조합한다.
        return AdminReservationDetailRes.builder().reservationId(reservationId).build();
    }

    // 관리자가 예약을 강제 취소한다.
    public AdminReservationCancelRes cancelReservationByAdmin(Long complexId, Long reservationId, AdminReservationCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) reservationId가 현재 complexId 소속인지 검증한다.
        // 3) 이미 취소/완료된 예약인지 상태를 검증한다.
        // 4) ADMIN 취소 사유와 cancelledAt을 저장한다.
        // 5) 예약 취소 알림 이벤트 outbox 적재를 수행한다.
        return AdminReservationCancelRes.builder()
                .reservationId(reservationId)
                .status(ReservationStatus.CANCELLED)
                .cancelReason(req.getReason())
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 시간이 지난 예약을 완료 처리한다.
    public ReservationCompleteRes completeReservations() {
        // TODO:
        // 1) endTime이 지난 CONFIRMED 예약을 조회한다.
        // 2) COMPLETED 상태와 completedAt을 저장한다.
        // 3) householdId 기준 facility_usage_monthly 집계 대상과 연결한다.
        // 4) 실제 배치/이벤트 발행은 2단계에서 구현한다.
        return ReservationCompleteRes.builder()
                .completedCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
