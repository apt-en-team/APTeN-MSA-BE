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
import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilitySeat;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.entity.ReservationTempHold;
import com.apten.facilityreservation.domain.enums.ReservationCancelReason;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.ReservationType;
import com.apten.facilityreservation.domain.repository.FacilityBlockTimeRepository;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilitySeatRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import com.apten.facilityreservation.domain.repository.ReservationTempHoldRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import com.apten.facilityreservation.infrastructure.redis.ReservationTempHoldRedisService;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
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

    private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(15);

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
    private final FacilityBlockTimeRepository facilityBlockTimeRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FacilitySeatRepository facilitySeatRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTempHoldRepository reservationTempHoldRepository;
    private final UserCacheRepository userCacheRepository;
    private final ReservationTempHoldRedisService reservationTempHoldRedisService;

    // 예약 가능 시간 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<AvailableTimeListRes> getAvailableTimeList(Long userId, Long complexId, AvailableTimeListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        if (req.getFacilityId() == null || req.getReservationDate() == null) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        Facility facility = facilityRepository.findByIdAndIsDeletedFalse(req.getFacilityId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));

        if (!facility.getComplexId().equals(complexId)) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND);
        }

        if (!Boolean.TRUE.equals(facility.getIsActive())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_INACTIVE);
        }

        FacilityPolicy policy = facilityPolicyRepository
                .findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, req.getFacilityId())
                .orElse(null);

        int slotMin = (policy != null && policy.getSlotMin() != null) ? policy.getSlotMin() : 30;
        Integer maxReservationCount = (policy != null) ? policy.getMaxReservationCount() : null;

        long openSec = facility.getOpenTime().toSecondOfDay();
        long closeSec = facility.getCloseTime().toSecondOfDay();
        long slotSec = (long) slotMin * 60;

        List<FacilityBlockTime> blockTimes = facilityBlockTimeRepository
                .findByFacilityIdAndBlockDateAndIsActiveTrue(req.getFacilityId(), req.getReservationDate());
        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatus(req.getFacilityId(), req.getReservationDate(), ReservationStatus.CONFIRMED);
        // 날짜 기준으로 유효 HOLDING을 한 번에 읽어 슬롯 계산에서 재사용한다.
        List<ReservationTempHold> activeHolds = reservationTempHoldRepository
                .findByFacilityIdAndReservationDateAndHoldStatusAndExpiresAtAfter(
                        req.getFacilityId(),
                        req.getReservationDate(),
                        ReservationHoldStatus.HOLDING,
                        LocalDateTime.now()
                );

        int seatTotalCount = 0;
        if (facility.getReservationType() == ReservationType.SEAT) {
            seatTotalCount = facilitySeatRepository.findByFacilityIdAndIsActiveTrue(req.getFacilityId()).size();
        }

        List<AvailableTimeListRes> result = new ArrayList<>();

        // 슬롯 생성: 정수 초 단위로 계산해 자정 wrap-around를 방지한다
        long slotStart = openSec;
        if (slotSec >= 86400) {
            // 슬롯이 하루 이상이면 운영 전체를 단일 슬롯으로 처리한다
            result.add(buildSlot(facility, blockTimes, confirmedReservations, activeHolds, maxReservationCount, seatTotalCount,
                    facility.getOpenTime(), facility.getCloseTime()));
        } else {
            while (slotStart + slotSec <= closeSec) {
                LocalTime start = LocalTime.ofSecondOfDay(slotStart);
                LocalTime end = LocalTime.ofSecondOfDay(slotStart + slotSec);
                result.add(buildSlot(facility, blockTimes, confirmedReservations, activeHolds, maxReservationCount, seatTotalCount, start, end));
                slotStart += slotSec;
            }
        }

        return result;
    }

    private AvailableTimeListRes buildSlot(
            Facility facility,
            List<FacilityBlockTime> blockTimes,
            List<Reservation> confirmedReservations,
            List<ReservationTempHold> activeHolds,
            Integer maxReservationCount,
            int seatTotalCount,
            LocalTime slotStart,
            LocalTime slotEnd) {

        // 시설 전체 차단 여부 — seatId=null이고 슬롯과 겹치는 차단 시간이 존재하면 차단
        boolean facilityBlocked = blockTimes.stream()
                .filter(b -> b.getSeatId() == null)
                .anyMatch(b -> isSlotOverlap(slotStart, slotEnd, b.getStartTime(), b.getEndTime()));

        if (facilityBlocked) {
            return AvailableTimeListRes.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .totalCount(null)
                    .availableCount(null)
                    .isReservable(false)
                    .build();
        }

        ReservationType type = facility.getReservationType();

        if (type == ReservationType.SEAT) {
            long confirmedCount = confirmedReservations.stream()
                    .filter(r -> slotStart.equals(r.getStartTime()) && slotEnd.equals(r.getEndTime()))
                    .count();
            long blockedSeatCount = blockTimes.stream()
                    .filter(b -> b.getSeatId() != null)
                    .filter(b -> isSlotOverlap(slotStart, slotEnd, b.getStartTime(), b.getEndTime()))
                    .count();
            // 유효 HOLDING도 좌석 점유로 간주해 잔여 좌석에서 차감한다.
            long holdingCount = activeHolds.stream()
                    .filter(h -> slotStart.equals(h.getStartTime()) && slotEnd.equals(h.getEndTime()))
                    .count();
            int availableCount = Math.max(0, seatTotalCount - (int) confirmedCount - (int) blockedSeatCount - (int) holdingCount);
            return AvailableTimeListRes.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .totalCount(seatTotalCount)
                    .availableCount(availableCount)
                    .isReservable(availableCount > 0)
                    .build();
        }

        if (type == ReservationType.COUNT) {
            long confirmedCount = confirmedReservations.stream()
                    .filter(r -> slotStart.equals(r.getStartTime()) && slotEnd.equals(r.getEndTime()))
                    .count();
            // COUNT형 HOLDING은 아직 생성하지 않으므로 현재는 0으로 본다.
            long holdingCount = 0L;
            Integer availableCount = maxReservationCount != null
                    ? Math.max(0, maxReservationCount - (int) confirmedCount - (int) holdingCount)
                    : null;
            boolean isReservable = maxReservationCount == null || availableCount > 0;
            return AvailableTimeListRes.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .totalCount(maxReservationCount)
                    .availableCount(availableCount)
                    .isReservable(isReservable)
                    .build();
        }

        // APPROVAL: 정원 제한 없음, 시설 차단이 없으면 항상 신청 가능
        return AvailableTimeListRes.builder()
                .startTime(slotStart)
                .endTime(slotEnd)
                .totalCount(null)
                .availableCount(null)
                .isReservable(true)
                .build();
    }

    private boolean isSlotOverlap(LocalTime slotStart, LocalTime slotEnd, LocalTime blockStart, LocalTime blockEnd) {
        LocalTime effectiveStart = blockStart != null ? blockStart : LocalTime.MIN;
        LocalTime effectiveEnd = blockEnd != null ? blockEnd : LocalTime.MAX;
        return slotStart.isBefore(effectiveEnd) && slotEnd.isAfter(effectiveStart);
    }

    // 좌석 임시 선점을 생성한다.
    @Transactional
    public SeatHoldPostRes createSeatHold(Long userId, Long complexId, SeatHoldPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        validateSeatHoldRequest(req);

        Facility facility = facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(req.getFacilityId(), complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));

        if (!Boolean.TRUE.equals(facility.getIsActive())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_INACTIVE);
        }
        if (facility.getReservationType() != ReservationType.SEAT) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        facilitySeatRepository.findByIdAndFacilityIdAndIsActiveTrue(req.getSeatId(), facility.getId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));

        validateSeatHoldTimeWindow(facility, req);
        validateSeatHoldBlockTime(facility.getId(), req);

        boolean alreadyReserved = reservationRepository
                .existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
                        facility.getId(),
                        req.getSeatId(),
                        req.getReservationDate(),
                        req.getStartTime(),
                        req.getEndTime(),
                        ReservationStatus.CONFIRMED
                );
        if (alreadyReserved) {
            throw new BusinessException(FacilityReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

        if (reservationTempHoldRedisService.existsHold(
                facility.getId(),
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime()
        )) {
            throw new BusinessException(FacilityReservationErrorCode.SEAT_TEMP_HOLD_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        if (reservationTempHoldRepository.existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndHoldStatusAndExpiresAtAfter(
                facility.getId(),
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime(),
                ReservationHoldStatus.HOLDING,
                now
        )) {
            throw new BusinessException(FacilityReservationErrorCode.SEAT_TEMP_HOLD_EXISTS);
        }

        boolean redisHeld = reservationTempHoldRedisService.tryHoldSeat(
                facility.getId(),
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime(),
                userId,
                SEAT_HOLD_TTL
        );
        if (!redisHeld) {
            throw new BusinessException(FacilityReservationErrorCode.SEAT_TEMP_HOLD_EXISTS);
        }

        LocalDateTime expiresAt = now.plus(SEAT_HOLD_TTL);
        try {
            ReservationTempHold hold = reservationTempHoldRepository.save(ReservationTempHold.builder()
                    .complexId(complexId)
                    .userId(userId)
                    .facilityId(facility.getId())
                    .seatId(req.getSeatId())
                    .reservationDate(req.getReservationDate())
                    .startTime(req.getStartTime())
                    .endTime(req.getEndTime())
                    .holdStatus(ReservationHoldStatus.HOLDING)
                    .expiresAt(expiresAt)
                    .build());

            return SeatHoldPostRes.builder()
                    .holdId(hold.getId())
                    .facilityId(hold.getFacilityId())
                    .seatId(hold.getSeatId())
                    .holdStatus(hold.getHoldStatus())
                    .expiresAt(hold.getExpiresAt())
                    .build();
        } catch (RuntimeException e) {
            // DB 저장 실패 시 Redis key를 바로 해제해 유령 선점이 남지 않게 한다.
            reservationTempHoldRedisService.releaseHold(
                    facility.getId(),
                    req.getSeatId(),
                    req.getReservationDate(),
                    req.getStartTime(),
                    req.getEndTime()
            );
            throw e;
        }
    }

    // 만료된 좌석 임시 선점을 자동 해제한다.
    @Transactional
    public TempHoldExpireRes expireSeatHolds() {
        LocalDateTime now = LocalDateTime.now();

        List<ReservationTempHold> expiredHolds = reservationTempHoldRepository
                .findByHoldStatusAndExpiresAtLessThanEqual(ReservationHoldStatus.HOLDING, now);

        expiredHolds.stream()
                .map(ReservationTempHold::getComplexId)
                .distinct()
                .forEach(complexId -> featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY));

        expiredHolds.forEach(ReservationTempHold::expire);

        return TempHoldExpireRes.builder()
                .expiredCount(expiredHolds.size())
                .processedAt(now)
                .build();
    }

    // 예약 생성을 처리한다.
    @Transactional
    public ReservationPostRes createReservation(Long userId, Long complexId, ReservationPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        validateReservationRequest(req);

        Facility facility = facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(req.getFacilityId(), complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));

        if (!Boolean.TRUE.equals(facility.getIsActive())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_INACTIVE);
        }

        validateReservationTimeWindow(facility, req.getStartTime(), req.getEndTime());
        validateReservationBlockTime(
                facility.getId(),
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime()
        );

        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        boolean duplicateReservation = reservationRepository
                .existsByUserIdAndFacilityIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
                        userId,
                        facility.getId(),
                        req.getReservationDate(),
                        req.getStartTime(),
                        req.getEndTime(),
                        ReservationStatus.CONFIRMED
                );
        if (duplicateReservation) {
            throw new BusinessException(FacilityReservationErrorCode.TIME_SLOT_NOT_AVAILABLE);
        }

        Reservation reservation;
        if (facility.getReservationType() == ReservationType.SEAT) {
            reservation = createSeatReservation(userId, complexId, facility, memberCache, req);
        } else if (facility.getReservationType() == ReservationType.COUNT) {
            reservation = createCountReservation(userId, complexId, facility, memberCache, req);
        } else {
            // 승인형 예약은 대기 상태 enum 정리 후 구현한다.
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        // TODO: 예약 생성 알림 / 이벤트 발행은 가은 담당과 연동 후 추가한다.

        return ReservationPostRes.builder()
                .reservationId(reservation.getId())
                .facilityId(reservation.getFacilityId())
                .seatId(reservation.getSeatId())
                .reservationDate(reservation.getReservationDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
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

    private void validateSeatHoldRequest(SeatHoldPostReq req) {
        if (req == null
                || req.getFacilityId() == null
                || req.getSeatId() == null
                || req.getReservationDate() == null
                || req.getStartTime() == null
                || req.getEndTime() == null
                || !req.getStartTime().isBefore(req.getEndTime())) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateSeatHoldTimeWindow(Facility facility, SeatHoldPostReq req) {
        validateReservationTimeWindow(facility, req.getStartTime(), req.getEndTime());
    }

    private void validateSeatHoldBlockTime(Long facilityId, SeatHoldPostReq req) {
        validateReservationBlockTime(
                facilityId,
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime()
        );
    }

    private void validateReservationRequest(ReservationPostReq req) {
        if (req == null
                || req.getFacilityId() == null
                || req.getReservationDate() == null
                || req.getStartTime() == null
                || req.getEndTime() == null
                || !req.getStartTime().isBefore(req.getEndTime())) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateReservationTimeWindow(Facility facility, LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(facility.getOpenTime())
                || endTime.isAfter(facility.getCloseTime())) {
            throw new BusinessException(FacilityReservationErrorCode.TIME_SLOT_NOT_AVAILABLE);
        }
    }

    private void validateReservationBlockTime(
            Long facilityId,
            Long seatId,
            java.time.LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        List<FacilityBlockTime> blockTimes = facilityBlockTimeRepository
                .findByFacilityIdAndBlockDateAndIsActiveTrue(facilityId, reservationDate);

        boolean blocked = blockTimes.stream()
                .filter(blockTime -> blockTime.getSeatId() == null || (seatId != null && blockTime.getSeatId().equals(seatId)))
                .anyMatch(blockTime -> isSlotOverlap(
                        startTime,
                        endTime,
                        blockTime.getStartTime(),
                        blockTime.getEndTime()
                ));

        if (blocked) {
            throw new BusinessException(FacilityReservationErrorCode.TIME_SLOT_NOT_AVAILABLE);
        }
    }

    private Reservation createSeatReservation(
            Long userId,
            Long complexId,
            Facility facility,
            HouseholdMemberCache memberCache,
            ReservationPostReq req
    ) {
        if (req.getHoldId() == null || req.getSeatId() == null) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        facilitySeatRepository.findByIdAndFacilityIdAndIsActiveTrue(req.getSeatId(), facility.getId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));

        ReservationTempHold hold = reservationTempHoldRepository
                .findByIdAndUserIdAndHoldStatus(req.getHoldId(), userId, ReservationHoldStatus.HOLDING)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.TEMP_HOLD_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (!hold.getExpiresAt().isAfter(now)) {
            throw new BusinessException(FacilityReservationErrorCode.TEMP_HOLD_EXPIRED);
        }

        boolean holdMatches = hold.getComplexId().equals(complexId)
                && hold.getFacilityId().equals(facility.getId())
                && hold.getSeatId().equals(req.getSeatId())
                && hold.getReservationDate().equals(req.getReservationDate())
                && hold.getStartTime().equals(req.getStartTime())
                && hold.getEndTime().equals(req.getEndTime());
        if (!holdMatches) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        if (!reservationTempHoldRedisService.validateHold(
                facility.getId(),
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime(),
                userId
        )) {
            throw new BusinessException(FacilityReservationErrorCode.TEMP_HOLD_EXPIRED);
        }

        boolean alreadyReserved = reservationRepository
                .existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
                        facility.getId(),
                        req.getSeatId(),
                        req.getReservationDate(),
                        req.getStartTime(),
                        req.getEndTime(),
                        ReservationStatus.CONFIRMED
                );
        if (alreadyReserved) {
            throw new BusinessException(FacilityReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .complexId(complexId)
                .userId(userId)
                .householdId(memberCache.getHouseholdId())
                .facilityId(facility.getId())
                .seatId(req.getSeatId())
                .reservationDate(req.getReservationDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build());

        hold.confirm();
        reservationTempHoldRedisService.releaseHold(
                facility.getId(),
                req.getSeatId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime()
        );

        return reservation;
    }

    private Reservation createCountReservation(
            Long userId,
            Long complexId,
            Facility facility,
            HouseholdMemberCache memberCache,
            ReservationPostReq req
    ) {
        FacilityPolicy policy = facilityPolicyRepository
                .findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, facility.getId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY));

        if (policy.getMaxReservationCount() == null || policy.getMaxReservationCount() <= 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        long confirmedCount = reservationRepository.countByFacilityIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
                facility.getId(),
                req.getReservationDate(),
                req.getStartTime(),
                req.getEndTime(),
                ReservationStatus.CONFIRMED
        );
        if (confirmedCount >= policy.getMaxReservationCount()) {
            throw new BusinessException(FacilityReservationErrorCode.RESERVATION_FULL);
        }

        return reservationRepository.save(Reservation.builder()
                .complexId(complexId)
                .userId(userId)
                .householdId(memberCache.getHouseholdId())
                .facilityId(facility.getId())
                .seatId(null)
                .reservationDate(req.getReservationDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build());
    }
}
