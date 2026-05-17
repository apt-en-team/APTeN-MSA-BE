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
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilitySeat;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.ReservationCancelReason;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilitySeatRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import com.apten.facilityreservation.infrastructure.redis.ReservationTempHoldRedisService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 일반 시설 예약과 좌석 선점 관련 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FacilitySeatRepository facilitySeatRepository;
    private final ReservationRepository reservationRepository;
    private final UserCacheRepository userCacheRepository;
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
    @Transactional(readOnly = true)
    public PageResponse<MyReservationListRes> getMyReservationList(Long userId, Long complexId, MyReservationListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        int page = req.getPage() != null ? Math.max(req.getPage(), 0) : 0;
        int size = req.getSize() != null && req.getSize() > 0 ? req.getSize() : 20;
        PageRequest pageable = PageRequest.of(page, size);

        // status enum null 비교 회피: null 여부에 따라 쿼리 분기
        Page<Reservation> reservationPage = (req.getStatus() != null)
                ? reservationRepository.findMyReservationsByStatus(userId, complexId, req.getStatus(), req.getFromDate(), req.getToDate(), pageable)
                : reservationRepository.findMyReservations(userId, complexId, req.getFromDate(), req.getToDate(), pageable);

        List<Reservation> reservations = reservationPage.getContent();

        // 시설명 batch 조회 (N+1 방지)
        Map<Long, Facility> facilityMap = reservations.isEmpty() ? Map.of() :
                facilityRepository.findAllById(
                                reservations.stream().map(Reservation::getFacilityId).distinct().toList())
                        .stream()
                        .collect(Collectors.toMap(Facility::getId, f -> f));

        // 좌석 번호 batch 조회 (N+1 방지)
        List<Long> seatIds = reservations.stream()
                .map(Reservation::getSeatId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, FacilitySeat> seatMap = seatIds.isEmpty() ? Map.of() :
                facilitySeatRepository.findAllById(seatIds)
                        .stream()
                        .collect(Collectors.toMap(FacilitySeat::getId, s -> s));

        List<MyReservationListRes> items = reservations.stream()
                .map(r -> {
                    Facility facility = facilityMap.get(r.getFacilityId());
                    FacilitySeat seat = r.getSeatId() != null ? seatMap.get(r.getSeatId()) : null;
                    return MyReservationListRes.builder()
                            .reservationId(r.getId())
                            .facilityName(facility != null ? facility.getName() : null)
                            .reservationDate(r.getReservationDate())
                            .startTime(r.getStartTime())
                            .endTime(r.getEndTime())
                            .seatNo(seat != null ? seat.getSeatNo() : null)
                            .status(r.getStatus())
                            .createdAt(r.getCreatedAt())
                            .build();
                })
                .toList();

        return PageResponse.<MyReservationListRes>builder()
                .content(items)
                .page(page)
                .size(size)
                .totalElements(reservationPage.getTotalElements())
                .totalPages(reservationPage.getTotalPages())
                .hasNext(reservationPage.hasNext())
                .build();
    }

    // 내 예약 상세를 조회한다.
    @Transactional(readOnly = true)
    public MyReservationDetailRes getMyReservationDetail(Long userId, Long complexId, Long reservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.RESERVATION_NOT_FOUND));

        // 요청 단지 소속 검증
        if (!reservation.getComplexId().equals(complexId)) {
            throw new BusinessException(FacilityReservationErrorCode.RESERVATION_OWNER_MISMATCH);
        }

        Facility facility = facilityRepository.findByIdAndIsDeletedFalse(reservation.getFacilityId())
                .orElse(null);

        FacilitySeat seat = (reservation.getSeatId() != null)
                ? facilitySeatRepository.findById(reservation.getSeatId()).orElse(null)
                : null;

        // 취소 가능 여부 — CONFIRMED 상태이고 정책 기준 취소 마감 이전인 경우
        boolean cancelable = false;
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            FacilityPolicy policy = facilityPolicyRepository
                    .findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, reservation.getFacilityId())
                    .orElse(null);
            int cancelDeadlineHours = (policy != null) ? policy.getCancelDeadlineHours() : 2;
            LocalDateTime reservationStart = LocalDateTime.of(reservation.getReservationDate(), reservation.getStartTime());
            cancelable = LocalDateTime.now().isBefore(reservationStart.minusHours(cancelDeadlineHours));
        }

        return MyReservationDetailRes.builder()
                .reservationId(reservation.getId())
                .facilityId(reservation.getFacilityId())
                .facilityName(facility != null ? facility.getName() : null)
                .seatNo(seat != null ? seat.getSeatNo() : null)
                .reservationDate(reservation.getReservationDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .cancelable(cancelable)
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // 내 예약을 취소한다.
    @Transactional
    public ReservationCancelRes cancelReservation(Long userId, Long complexId, Long reservationId, ReservationCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.RESERVATION_NOT_FOUND));

        // 요청 단지 소속 검증
        if (!reservation.getComplexId().equals(complexId)) {
            throw new BusinessException(FacilityReservationErrorCode.RESERVATION_OWNER_MISMATCH);
        }

        // 이미 취소/완료 상태면 처리 불가
        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 정책 기준 취소 마감 검증
        FacilityPolicy policy = facilityPolicyRepository
                .findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, reservation.getFacilityId())
                .orElse(null);
        int cancelDeadlineHours = (policy != null) ? policy.getCancelDeadlineHours() : 2;
        LocalDateTime reservationStart = LocalDateTime.of(reservation.getReservationDate(), reservation.getStartTime());
        if (!LocalDateTime.now().isBefore(reservationStart.minusHours(cancelDeadlineHours))) {
            throw new BusinessException(FacilityReservationErrorCode.CANCEL_TIME_EXPIRED);
        }

        reservation.cancel(ReservationCancelReason.USER);

        // TODO: 예약 취소 알림 발행 (가은 담당)

        return ReservationCancelRes.builder()
                .reservationId(reservation.getId())
                .status(reservation.getStatus())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }

    // 관리자 예약 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<AdminReservationListRes> getAdminReservationList(Long complexId, AdminReservationListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        int page = req.getPage() != null ? Math.max(req.getPage(), 0) : 0;
        int size = req.getSize() != null && req.getSize() > 0 ? req.getSize() : 20;
        PageRequest pageable = PageRequest.of(page, size);

        // status enum null 비교 회피: null 여부에 따라 쿼리 분기
        Page<Reservation> reservationPage = (req.getStatus() != null)
                ? reservationRepository.findAdminReservationsByStatus(complexId, req.getStatus(), req.getFacilityId(), req.getReservationDate(), pageable)
                : reservationRepository.findAdminReservations(complexId, req.getFacilityId(), req.getReservationDate(), pageable);

        List<Reservation> reservations = reservationPage.getContent();

        // 시설명 batch 조회 (N+1 방지)
        Map<Long, Facility> facilityMap = reservations.isEmpty() ? Map.of() :
                facilityRepository.findAllById(
                                reservations.stream().map(Reservation::getFacilityId).distinct().toList())
                        .stream()
                        .collect(Collectors.toMap(Facility::getId, f -> f));

        // 좌석 번호 batch 조회 (N+1 방지)
        List<Long> seatIds = reservations.stream()
                .map(Reservation::getSeatId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, FacilitySeat> seatMap = seatIds.isEmpty() ? Map.of() :
                facilitySeatRepository.findAllById(seatIds)
                        .stream()
                        .collect(Collectors.toMap(FacilitySeat::getId, s -> s));

        // 입주민 이름 batch 조회 (N+1 방지)
        List<Long> userIds = reservations.stream()
                .map(Reservation::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds)
                        .stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));

        List<AdminReservationListRes> items = reservations.stream()
                .map(r -> {
                    Facility facility = facilityMap.get(r.getFacilityId());
                    FacilitySeat seat = r.getSeatId() != null ? seatMap.get(r.getSeatId()) : null;
                    UserCache user = r.getUserId() != null ? userMap.get(r.getUserId()) : null;
                    return AdminReservationListRes.builder()
                            .reservationId(r.getId())
                            .facilityId(r.getFacilityId())
                            .facilityName(facility != null ? facility.getName() : null)
                            .userId(r.getUserId())
                            .residentName(user != null ? user.getName() : null)
                            .reservationDate(r.getReservationDate())
                            .startTime(r.getStartTime())
                            .endTime(r.getEndTime())
                            .seatNo(seat != null ? seat.getSeatNo() : null)
                            .status(r.getStatus())
                            .createdAt(r.getCreatedAt())
                            .build();
                })
                .toList();

        return PageResponse.<AdminReservationListRes>builder()
                .content(items)
                .page(page)
                .size(size)
                .totalElements(reservationPage.getTotalElements())
                .totalPages(reservationPage.getTotalPages())
                .hasNext(reservationPage.hasNext())
                .build();
    }

    // 관리자 예약 상세를 조회한다.
    @Transactional(readOnly = true)
    public AdminReservationDetailRes getAdminReservationDetail(Long complexId, Long reservationId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        Reservation reservation = reservationRepository.findByIdAndComplexId(reservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.RESERVATION_NOT_FOUND));

        Facility facility = facilityRepository.findByIdAndIsDeletedFalse(reservation.getFacilityId())
                .orElse(null);

        FacilitySeat seat = (reservation.getSeatId() != null)
                ? facilitySeatRepository.findById(reservation.getSeatId()).orElse(null)
                : null;

        UserCache user = (reservation.getUserId() != null)
                ? userCacheRepository.findById(reservation.getUserId()).orElse(null)
                : null;

        return AdminReservationDetailRes.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUserId())
                .residentName(user != null ? user.getName() : null)
                .facilityId(reservation.getFacilityId())
                .facilityName(facility != null ? facility.getName() : null)
                .reservationDate(reservation.getReservationDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .seatNo(seat != null ? seat.getSeatNo() : null)
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .completedAt(reservation.getCompletedAt())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // 관리자가 예약을 강제 취소한다.
    @Transactional
    public AdminReservationCancelRes cancelReservationByAdmin(Long complexId, Long reservationId, AdminReservationCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        Reservation reservation = reservationRepository.findByIdAndComplexId(reservationId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.RESERVATION_NOT_FOUND));

        // 이미 취소/완료 상태면 처리 불가
        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 관리자 강제 취소 — 취소 마감 시간 무관
        reservation.cancel(ReservationCancelReason.ADMIN);

        // TODO: 예약 강제 취소 알림 발행 (가은 담당)

        return AdminReservationCancelRes.builder()
                .reservationId(reservation.getId())
                .status(reservation.getStatus())
                .cancelReason(req.getReason())
                .cancelledAt(reservation.getCancelledAt())
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
