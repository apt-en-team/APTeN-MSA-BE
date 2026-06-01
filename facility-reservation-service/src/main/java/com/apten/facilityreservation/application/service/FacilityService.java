package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.facilityreservation.application.model.request.CountStatusReq;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeBatchPostReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeListReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityClosureRulePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityClosureRulePostReq;
import com.apten.facilityreservation.application.model.request.FacilityListReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPatchReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatBulkPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPostReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityTypeListReq;
import com.apten.facilityreservation.application.model.request.FacilityUsageStatusReq;
import com.apten.facilityreservation.application.model.request.ResidentFacilityListReq;
import com.apten.facilityreservation.application.model.request.SeatStatusReq;
import com.apten.facilityreservation.application.model.response.*;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
import com.apten.facilityreservation.domain.entity.FacilityClosureRule;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilitySeat;
import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.entity.ReservationTempHold;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.enums.ReservationType;
import com.apten.facilityreservation.domain.repository.*;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설/좌석/차단 시간 관리
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FacilityService {

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
    private final FacilityTypeRepository facilityTypeRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FacilitySeatRepository facilitySeatRepository;
    private final ReservationRepository reservationRepository;
    private final ComplexCacheRepository complexCacheRepository;
    private final FacilityBlockTimeRepository facilityBlockTimeRepository;
    private final FacilityClosureRuleRepository facilityClosureRuleRepository;
    private final ReservationTempHoldRepository reservationTempHoldRepository;
    private final UserCacheRepository userCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;


    // 시설 관리자 접근 검증
    private void validateAdminAccess(Long complexId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        complexCacheRepository.findByIdAndStatus(complexId, ComplexCacheStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.COMPLEX_NOT_FOUND));
    }

    // 입주민 시설 접근 검증
    private void validateResidentAccess(Long complexId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        complexCacheRepository.findByIdAndStatus(complexId, ComplexCacheStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.COMPLEX_NOT_FOUND));
    }

    // 시설 조회
    private Facility getFacility(Long complexId, Long facilityId) {
        if (facilityId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        return facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(facilityId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));
    }

    // 입주민 활성 시설 조회
    private Facility getResidentFacility(Long complexId, Long facilityId) {
        Facility facility = getFacility(complexId, facilityId);
        if (Boolean.FALSE.equals(facility.getIsActive())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_INACTIVE);
        }
        return facility;
    }

    // 시설 타입 조회
    private FacilityType getFacilityType(Long typeId) {
        if (typeId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        return facilityTypeRepository.findById(typeId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_TYPE_NOT_FOUND));
    }

    // 시설 등록 요청 검증
    private void validateCreateReq(FacilityPostReq req) {
        if (req == null || req.getName() == null || req.getName().isBlank() || req.getTypeId() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        if (req.getMaxCount() != null && req.getMaxCount() < 1) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 시설 수정 요청 검증
    private void validatePatchReq(FacilityPatchReq req) {
        if (req == null || req.getName() == null || req.getName().isBlank()
                || req.getTypeId() == null || req.getReservationType() == null
                || req.getOpenTime() == null || req.getCloseTime() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        if (req.getMaxCount() != null && req.getMaxCount() < 1) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 시설 활성 변경 요청 검증
    private void validateActiveReq(FacilityActivePatchReq req) {
        if (req == null || req.getIsActive() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 운영 시간 검증 (야간 운영 허용)
    private void validateTime(java.time.LocalTime openTime, java.time.LocalTime closeTime) {
        if (openTime == null || closeTime == null || openTime.equals(closeTime)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 현황 조회 시간 범위 검증 (야간 운영 허용)
    private void validateSlotRange(LocalDate targetDate, LocalTime startTime, LocalTime endTime) {
        if (targetDate == null || startTime == null || endTime == null || startTime.equals(endTime)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 시간 구간 겹침 판별 (야간 운영 보정)
    private boolean isTimeOverlap(LocalTime slotStart, LocalTime slotEnd, LocalTime candidateStart, LocalTime candidateEnd) {
        LocalTime effectiveStart = candidateStart != null ? candidateStart : LocalTime.MIN;
        LocalTime effectiveEnd = (candidateEnd != null && candidateEnd.isAfter(effectiveStart)) ? candidateEnd : LocalTime.MAX;
        LocalTime effectiveSlotEnd = slotEnd.isAfter(slotStart) ? slotEnd : LocalTime.MAX;
        return slotStart.isBefore(effectiveEnd) && effectiveSlotEnd.isAfter(effectiveStart);
    }

    // 시설 차단 시간 요청 검증
    private void validateBlockTimeReq(FacilityBlockTimePostReq req) {
        if (req == null || req.getBlockDate() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        boolean hasStartTime = req.getStartTime() != null;
        boolean hasEndTime = req.getEndTime() != null;

        if (hasStartTime != hasEndTime) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (hasStartTime && !req.getStartTime().isBefore(req.getEndTime())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 페이지 응답 변환
    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 예약 단위 표시명 생성
    private String buildReservationUnitLabel(FacilityPolicy policy) {
        if (policy == null || policy.getUsageUnitType() == null) {
            return null;
        }
        return switch (policy.getUsageUnitType()) {
            case MINUTE -> policy.getSlotMin() != null ? policy.getSlotMin() + "분" : null;
            case DAY -> "하루";
        };
    }

    // 관리자 시설 등록
    @Transactional
    public FacilityPostRes createFacility(Long complexId, FacilityPostReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 등록 요청 필수값 검증
        validateCreateReq(req);

        // 시설 타입 존재 검증
        getFacilityType(req.getTypeId());

        // 예약 방식 기본값 처리
        ReservationType reservationType = req.getReservationType() != null
                ? req.getReservationType()
                : ReservationType.COUNT;

        // 운영 시간 검증
        validateTime(req.getOpenTime(), req.getCloseTime());

        // 시설 엔티티 생성 및 저장
        Facility savedFacility = facilityRepository.save(Facility.builder()
                .complexId(complexId)
                .typeId(req.getTypeId())
                .name(req.getName())
                .description(req.getDescription())
                .reservationType(reservationType)
                .openTime(req.getOpenTime())
                .closeTime(req.getCloseTime())
                .maxCount(req.getMaxCount())
                .isActive(req.getIsActive() == null || req.getIsActive())
                .isDeleted(false)
                .build());

        // 등록 응답 변환
        return FacilityPostRes.builder()
                .facilityId(savedFacility.getId())
                .name(savedFacility.getName())
                .reservationType(savedFacility.getReservationType())
                .isActive(savedFacility.getIsActive())
                .createdAt(savedFacility.getCreatedAt())
                .build();
    }

    // 관리자 시설 목록 조회
    public PageResponse<FacilityListRes> getAdminFacilityList(Long complexId, FacilityListReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 페이지 번호 보정
        int page = Math.max(req.getPage(), 0);

        // 페이지 크기 보정
        int size = req.getSize() > 0 ? req.getSize() : 20;

        // 페이지 조건 생성
        PageRequest pageRequest = PageRequest.of(page, size);

        // 예약 방식 필터 여부에 따라 쿼리 분기
        Page<Facility> facilityPage = req.getReservationType() == null
                ? facilityRepository.findAdminFacilities(
                complexId,
                req.getTypeId(),
                req.getIsActive(),
                pageRequest
        )
                : facilityRepository.findAdminFacilitiesByReservationType(
                complexId,
                req.getTypeId(),
                req.getReservationType(),
                req.getIsActive(),
                pageRequest
        );

        List<Facility> facilities = facilityPage.getContent();
        List<Long> facilityIds = facilities.stream().map(Facility::getId).toList();

        // 시설 타입명 일괄 조회 (N+1 방지)
        List<Long> typeIds = facilities.stream().map(Facility::getTypeId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> typeNameMap = typeIds.isEmpty() ? Map.of() :
                facilityTypeRepository.findAllById(typeIds)
                        .stream()
                        .collect(Collectors.toMap(FacilityType::getId, FacilityType::getTypeName));

        // 정책 일괄 조회 (N+1 방지)
        Map<Long, FacilityPolicy> policyMap = facilityIds.isEmpty() ? Map.of() :
                facilityPolicyRepository.findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                        .stream()
                        .collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        // 오늘 예약 수 일괄 조회 (N+1 방지)
        LocalDate today = LocalDate.now();
        List<ReservationStatus> activeStatuses = List.of(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED);
        Map<Long, Integer> todayCountMap = facilityIds.isEmpty() ? Map.of() :
                reservationRepository.countGroupByFacilityId(facilityIds, today, activeStatuses)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> ((Long) row[1]).intValue()
                        ));

        // 오늘 임시 차단 시설 일괄 조회
        Set<Long> blockFacilityIds = facilityIds.isEmpty() ? Set.of() :
                new java.util.HashSet<>(facilityBlockTimeRepository.findBlockedFacilityIds(facilityIds, today));

        // 오늘 정기 휴무 시설 일괄 조회
        Set<Long> closureFacilityIds = new java.util.HashSet<>();
        if (!facilityIds.isEmpty()) {
            facilityClosureRuleRepository.findByFacilityIdInAndIsActiveTrue(facilityIds).stream()
                    .filter(rule -> rule.isDateBlocked(today))
                    .map(FacilityClosureRule::getFacilityId)
                    .forEach(closureFacilityIds::add);
        }

        // 오늘 차단 시설 통합
        Set<Long> blockedFacilityIds = new java.util.HashSet<>(blockFacilityIds);
        blockedFacilityIds.addAll(closureFacilityIds);

        // 목록 응답 변환
        List<FacilityListRes> items = facilities.stream()
                .map(facility -> {
                    FacilityPolicy policy = policyMap.get(facility.getId());
                    return FacilityListRes.builder()
                            .facilityId(facility.getId())
                            .typeId(facility.getTypeId())
                            .typeName(typeNameMap.get(facility.getTypeId()))
                            .name(facility.getName())
                            .reservationType(facility.getReservationType())
                            .openTime(facility.getOpenTime())
                            .closeTime(facility.getCloseTime())
                            .isActive(facility.getIsActive())
                            .maxCount(facility.getMaxCount())
                            .baseFee(policy != null ? policy.getBaseFee() : null)
                            .slotMin(policy != null ? policy.getSlotMin() : null)
                            .usageUnitType(policy != null ? policy.getUsageUnitType() : null)
                            .usageUnitTypeName(policy != null && policy.getUsageUnitType() != null
                                    ? policy.getUsageUnitType().getLabel() : null)
                            .reservationUnitLabel(buildReservationUnitLabel(policy))
                            .todayReservedCount(todayCountMap.getOrDefault(facility.getId(), 0))
                            .isTodayBlocked(blockedFacilityIds.contains(facility.getId()))
                            .blockType(closureFacilityIds.contains(facility.getId()) ? "CLOSURE"
                                    : blockFacilityIds.contains(facility.getId()) ? "BLOCK"
                                    : null)
                            .build();
                })
                .toList();

        // 페이지 응답 변환
        return PageResponse.<FacilityListRes>builder()
                .content(items)
                .page(page)
                .size(size)
                .totalElements(facilityPage.getTotalElements())
                .totalPages(facilityPage.getTotalPages())
                .hasNext(facilityPage.hasNext())
                .build();
    }

    // 관리자 시설 상세 조회
    public FacilityDetailRes getAdminFacilityDetail(Long complexId, Long facilityId) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 시설 소속 및 삭제 여부 검증
        Facility facility = getFacility(complexId, facilityId);

        // 시설 타입 정보 조회
        FacilityType facilityType = getFacilityType(facility.getTypeId());

        // 활성 정책 조회 (없으면 null)
        FacilityPolicy policy = facilityPolicyRepository
                .findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, facility.getId())
                .orElse(null);

        // 좌석 목록 기본값
        List<FacilityDetailRes.SeatItem> seats = List.of();

        // 좌석형 시설 좌석 목록 조회
        if (facility.getReservationType() == ReservationType.SEAT) {
            seats = facilitySeatRepository.findByFacilityIdOrderBySeatNoAsc(facility.getId())
                    .stream()
                    .map(seat -> FacilityDetailRes.SeatItem.builder()
                            .seatId(seat.getId())
                            .seatNo(seat.getSeatNo())
                            .seatName(seat.getSeatName())
                            .isActive(seat.getIsActive())
                            .build())
                    .toList();
        }

        return FacilityDetailRes.builder()
                .facilityId(facility.getId())
                .typeId(facility.getTypeId())
                .typeName(facilityType.getTypeName())
                .name(facility.getName())
                .description(facility.getDescription())
                .reservationType(facility.getReservationType())
                .openTime(facility.getOpenTime())
                .closeTime(facility.getCloseTime())
                .isActive(facility.getIsActive())
                .maxCount(facility.getMaxCount())
                .baseFee(policy != null ? policy.getBaseFee() : null)
                .slotMin(policy != null ? policy.getSlotMin() : null)
                .usageUnitType(policy != null ? policy.getUsageUnitType() : null)
                .usageUnitTypeName(policy != null && policy.getUsageUnitType() != null
                        ? policy.getUsageUnitType().getLabel() : null)
                .reservationUnitLabel(buildReservationUnitLabel(policy))
                .createdAt(facility.getCreatedAt())
                .updatedAt(facility.getUpdatedAt())
                .seats(seats)
                .build();
    }

    // 관리자 시설 수정
    @Transactional
    public FacilityPatchRes updateFacility(Long complexId, Long facilityId, FacilityPatchReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 수정 요청 검증
        validatePatchReq(req);

        Facility facility = getFacility(complexId, facilityId);

        // 시설 타입 검증
        getFacilityType(req.getTypeId());

        // 운영 시간 검증
        validateTime(req.getOpenTime(), req.getCloseTime());

        facility.apply(req);

        return FacilityPatchRes.builder()
                .facilityId(facility.getId())
                .name(facility.getName())
                .updatedAt(facility.getUpdatedAt())
                .build();
    }

    // 관리자 시설 삭제
    @Transactional
    public FacilityDeleteRes deleteFacility(Long complexId, Long facilityId) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        Facility facility = getFacility(complexId, facilityId);

        // 예약 존재 검증
        if (reservationRepository.existsByFacilityId(facility.getId())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_HAS_RESERVATION);
        }

        facility.softDelete();

        return FacilityDeleteRes.builder()
                .facilityId(facility.getId())
                .isDeleted(facility.getIsDeleted())
                .deletedAt(facility.getDeletedAt())
                .build();
    }

    // 관리자 시설 활성 상태 변경
    @Transactional
    public FacilityActivePatchRes changeFacilityActive(Long complexId, Long facilityId, FacilityActivePatchReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 활성 변경 요청 검증
        validateActiveReq(req);

        Facility facility = getFacility(complexId, facilityId);
        facility.changeActive(req);

        return FacilityActivePatchRes.builder()
                .facilityId(facility.getId())
                .isActive(facility.getIsActive())
                .updatedAt(facility.getUpdatedAt())
                .build();
    }

    // 시설 타입 목록 조회
    public List<FacilityTypeListRes> getFacilityTypeList(Long complexId, FacilityTypeListReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 활성 여부 조건 확인
        Boolean isActive = req == null ? null : req.getIsActive();

        // 시설 타입 목록 조회
        List<FacilityType> facilityTypes = isActive == null
                ? facilityTypeRepository.findAllByOrderByIdAsc()
                : facilityTypeRepository.findByIsActiveOrderByIdAsc(isActive);

        // 시설 타입 응답 변환
        return facilityTypes.stream()
                .map(type -> FacilityTypeListRes.builder()
                        .facilityTypeId(type.getId())
                        .typeCode(type.getTypeCode())
                        .typeName(type.getTypeName())
                        .description(type.getDescription())
                        .isActive(type.getIsActive())
                        .build())
                .toList();
    }

//    // 시설 타입을 수정한다.
//    public FacilityTypePatchRes updateFacilityType(Long complexId, Long facilityTypeId, FacilityTypePatchReq req) {
//        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
//        return FacilityTypePatchRes.builder()
//                .facilityTypeId(facilityTypeId)
//                .typeName(req.getTypeName())
//                .isActive(req.getIsActive())
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }

    // 시설 차단 시간 등록
    @Transactional
    public FacilityBlockTimePostRes createFacilityBlockTime(Long complexId, Long facilityId, FacilityBlockTimePostReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 시설 소속 검증
        Facility facility = getFacility(complexId, facilityId);

        // 차단 시간 요청 검증
        validateBlockTimeReq(req);

        // 좌석 소속 검증
        if (req.getSeatId() != null) {
            FacilitySeat seat = facilitySeatRepository.findById(req.getSeatId())
                    .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));
            if (!seat.getFacilityId().equals(facility.getId())) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
        }

        // 차단 시간 저장
        FacilityBlockTime blockTime = facilityBlockTimeRepository.save(FacilityBlockTime.builder()
                .facilityId(facility.getId())
                .seatId(req.getSeatId())
                .blockDate(req.getBlockDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .reason(req.getReason())
                .isActive(true)
                .build());

        // 차단 시간 등록 응답
        return FacilityBlockTimePostRes.builder()
                .facilityBlockTimeId(blockTime.getId())
                .facilityId(blockTime.getFacilityId())
                .blockDate(blockTime.getBlockDate())
                .startTime(blockTime.getStartTime())
                .endTime(blockTime.getEndTime())
                .reason(blockTime.getReason())
                .isActive(blockTime.getIsActive())
                .createdAt(blockTime.getCreatedAt())
                .build();
    }

    // 시설 차단 시간 목록 조회
    public List<FacilityBlockTimeListRes> getFacilityBlockTimeList(
            Long complexId,
            Long facilityId,
            FacilityBlockTimeListReq req
    ) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 시설 소속 검증
        Facility facility = getFacility(complexId, facilityId);

        // 조회 기간 검증
        if (req != null && req.getFromDate() != null && req.getToDate() != null
                && req.getFromDate().isAfter(req.getToDate())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 조회 조건 정리
        LocalDate fromDate = req == null ? null : req.getFromDate();
        LocalDate toDate = req == null ? null : req.getToDate();
        Boolean isActive = req == null ? null : req.getIsActive();

        // 차단 시간 목록 조회 및 응답 변환
        List<FacilityBlockTime> blockTimes = facilityBlockTimeRepository.findBlockTimes(
                facility.getId(),
                fromDate,
                toDate,
                isActive
        );

        // 연관 좌석 일괄 조회 (N+1 방지)
        Set<Long> seatIds = blockTimes.stream()
                .map(FacilityBlockTime::getSeatId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, FacilitySeat> seatMap = seatIds.isEmpty()
                ? Map.of()
                : facilitySeatRepository.findAllById(seatIds)
                        .stream()
                        .collect(Collectors.toMap(FacilitySeat::getId, s -> s));

        return blockTimes.stream()
                .map(blockTime -> {
                    FacilitySeat seat = blockTime.getSeatId() != null ? seatMap.get(blockTime.getSeatId()) : null;
                    return FacilityBlockTimeListRes.builder()
                            .facilityBlockTimeId(blockTime.getId())
                            .facilityId(blockTime.getFacilityId())
                            .seatId(blockTime.getSeatId())
                            .seatNo(seat != null ? seat.getSeatNo() : null)
                            .seatName(seat != null ? seat.getSeatName() : null)
                            .blockDate(blockTime.getBlockDate())
                            .startTime(blockTime.getStartTime())
                            .endTime(blockTime.getEndTime())
                            .reason(blockTime.getReason())
                            .isActive(blockTime.getIsActive())
                            .batchId(blockTime.getBatchId())
                            .build();
                })
                .toList();
    }

    // 반복 차단 시간 배치 등록 (요일 + 기간)
    @Transactional
    public FacilityBlockTimeBatchPostRes createFacilityBlockTimeBatch(Long complexId, Long facilityId, FacilityBlockTimeBatchPostReq req) {
        validateAdminAccess(complexId);
        Facility facility = getFacility(complexId, facilityId);

        if (req == null || req.getDaysOfWeek() == null || req.getDaysOfWeek().isEmpty()
                || req.getValidFrom() == null || req.getValidUntil() == null
                || req.getValidFrom().isAfter(req.getValidUntil())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        boolean hasStart = req.getStartTime() != null;
        boolean hasEnd = req.getEndTime() != null;
        if (hasStart != hasEnd || (hasStart && !req.getStartTime().isBefore(req.getEndTime()))) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (req.getSeatId() != null) {
            FacilitySeat seat = facilitySeatRepository.findById(req.getSeatId())
                    .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));
            if (!seat.getFacilityId().equals(facility.getId())) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
        }

        // 차단 배치 그룹 식별자 생성
        long batchId = UUID.randomUUID().getLeastSignificantBits() & 0x7FFFFFFFFFFFFFFFL;

        List<FacilityBlockTime> blockTimes = new ArrayList<>();
        LocalDate current = req.getValidFrom();
        while (!current.isAfter(req.getValidUntil())) {
            if (req.getDaysOfWeek().contains(current.getDayOfWeek())) {
                blockTimes.add(FacilityBlockTime.builder()
                        .facilityId(facility.getId())
                        .seatId(req.getSeatId())
                        .blockDate(current)
                        .startTime(req.getStartTime())
                        .endTime(req.getEndTime())
                        .reason(req.getReason())
                        .batchId(batchId)
                        .isActive(true)
                        .build());
            }
            current = current.plusDays(1);
        }

        if (blockTimes.isEmpty()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        facilityBlockTimeRepository.saveAll(blockTimes);

        return FacilityBlockTimeBatchPostRes.builder()
                .batchId(batchId)
                .facilityId(facility.getId())
                .createdCount(blockTimes.size())
                .validFrom(req.getValidFrom())
                .validUntil(req.getValidUntil())
                .daysOfWeek(req.getDaysOfWeek())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 반복 차단 그룹 비활성화 (batchId 기준)
    @Transactional
    public FacilityBlockTimeBatchDeactivateRes deactivateFacilityBlockTimeBatch(Long complexId, Long facilityId, Long batchId) {
        validateAdminAccess(complexId);
        Facility facility = getFacility(complexId, facilityId);
        int deactivatedCount = facilityBlockTimeRepository.deactivateByBatchIdAndFacilityId(batchId, facility.getId());
        return FacilityBlockTimeBatchDeactivateRes.builder()
                .batchId(batchId)
                .deactivatedCount(deactivatedCount)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // 시설 차단 시간 단건 비활성화
    @Transactional
    public FacilityBlockTimeDeactivateRes deactivateFacilityBlockTime(Long complexId, Long facilityId, Long blockTimeId) {
        validateAdminAccess(complexId);
        Facility facility = getFacility(complexId, facilityId);
        FacilityBlockTime blockTime = facilityBlockTimeRepository.findById(blockTimeId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));
        if (!blockTime.getFacilityId().equals(facility.getId())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        blockTime.deactivate();
        return FacilityBlockTimeDeactivateRes.builder()
                .facilityBlockTimeId(blockTime.getId())
                .processedAt(LocalDateTime.now())
                .build();
    }

    // 시설 차단 시간 단건 수정
    @Transactional
    public FacilityBlockTimePatchRes updateFacilityBlockTime(Long complexId, Long facilityId, Long blockTimeId, FacilityBlockTimePatchReq req) {
        validateAdminAccess(complexId);
        Facility facility = getFacility(complexId, facilityId);
        FacilityBlockTime blockTime = facilityBlockTimeRepository.findById(blockTimeId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));
        if (!blockTime.getFacilityId().equals(facility.getId())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        blockTime.updateSchedule(req.getBlockDate(), req.getStartTime(), req.getEndTime());
        return FacilityBlockTimePatchRes.builder()
                .facilityBlockTimeId(blockTime.getId())
                .blockDate(blockTime.getBlockDate())
                .startTime(blockTime.getStartTime())
                .endTime(blockTime.getEndTime())
                .isActive(blockTime.getIsActive())
                .build();
    }

    // 시설 좌석 등록
    @Transactional
    public FacilitySeatPostRes createFacilitySeat(Long complexId, Long facilityId, FacilitySeatPostReq req) {
        validateAdminAccess(complexId);

        Facility facility = getFacility(complexId, facilityId);

        if (facility.getReservationType() != ReservationType.SEAT) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        if (req.getSeatNo() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (facilitySeatRepository.existsByFacilityIdAndSeatNo(facilityId, req.getSeatNo())) {
            throw new BusinessException(FacilityReservationErrorCode.DUPLICATE_SEAT);
        }

        FacilitySeat seat = facilitySeatRepository.save(FacilitySeat.builder()
                .facilityId(facilityId)
                .seatNo(req.getSeatNo())
                .seatName(req.getSeatName())
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .isActive(req.getIsActive() == null || req.getIsActive())
                .build());

        return FacilitySeatPostRes.builder()
                .seatId(seat.getId())
                .facilityId(seat.getFacilityId())
                .seatNo(seat.getSeatNo())
                .seatName(seat.getSeatName())
                .isActive(seat.getIsActive())
                .createdAt(seat.getCreatedAt())
                .build();
    }

    // 좌석 라벨 규칙 적용
    private String resolveSeatLabel(String prefix, Integer seatNo) {
        return prefix + "-" + seatNo;
    }

    // 좌석 기본 이름 생성
    private String resolveSeatName(String seatNamePattern, String label, Integer seatNo) {
        String effectivePattern = seatNamePattern == null || seatNamePattern.isBlank()
                ? "{label} 좌석"
                : seatNamePattern;
        return effectivePattern
                .replace("{seatNo}", String.valueOf(seatNo))
                .replace("{label}", label);
    }

    // 시설 좌석 일괄 등록
    @Transactional
    public FacilitySeatBulkPostRes createFacilitySeatsBulk(Long complexId, Long facilityId, FacilitySeatBulkPostReq req) {
        validateAdminAccess(complexId);

        Facility facility = getFacility(complexId, facilityId);

        if (facility.getReservationType() != ReservationType.SEAT) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        if (req == null || req.getPrefix() == null || req.getPrefix().isBlank()
                || req.getStartNo() == null || req.getEndNo() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (req.getStartNo() > req.getEndNo()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        int createdCount = req.getEndNo() - req.getStartNo() + 1;
        if (createdCount > 100) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        List<Integer> seatNos = new ArrayList<>(createdCount);
        for (int seatNo = req.getStartNo(); seatNo <= req.getEndNo(); seatNo++) {
            seatNos.add(seatNo);
        }

        // 좌석 일괄 중복 방지
        if (!facilitySeatRepository.findByFacilityIdAndSeatNoIn(facilityId, seatNos).isEmpty()) {
            throw new BusinessException(FacilityReservationErrorCode.DUPLICATE_SEAT);
        }

        List<FacilitySeat> seats = seatNos.stream()
                .map(seatNo -> {
                    String label = resolveSeatLabel(req.getPrefix().trim(), seatNo);
                    return FacilitySeat.builder()
                            .facilityId(facilityId)
                            .seatNo(seatNo)
                            .seatName(resolveSeatName(req.getSeatNamePattern(), label, seatNo))
                            .sortOrder(seatNo)
                            .isActive(req.getIsActive() == null || req.getIsActive())
                            .build();
                })
                .toList();

        facilitySeatRepository.saveAll(seats);

        return FacilitySeatBulkPostRes.builder()
                .createdCount(createdCount)
                .firstSeatNo(req.getStartNo())
                .lastSeatNo(req.getEndNo())
                .build();
    }

    // 시설 좌석 목록 조회
    public List<FacilitySeatListRes> getFacilitySeatList(Long complexId, Long facilityId) {
        validateAdminAccess(complexId);

        getFacility(complexId, facilityId);

        return facilitySeatRepository.findByFacilityIdOrderBySeatNoAsc(facilityId)
                .stream()
                .map(seat -> FacilitySeatListRes.builder()
                        .seatId(seat.getId())
                        .seatNo(seat.getSeatNo())
                        .seatName(seat.getSeatName())
                        .sortOrder(seat.getSortOrder())
                        .isActive(seat.getIsActive())
                        .build())
                .toList();
    }

    // 시설 좌석 수정
    @Transactional
    public FacilitySeatPatchRes updateFacilitySeat(Long complexId, Long seatId, FacilitySeatPatchReq req) {
        validateAdminAccess(complexId);

        FacilitySeat seat = facilitySeatRepository.findById(seatId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));

        // 좌석 시설 소속 검증
        getFacility(complexId, seat.getFacilityId());

        seat.apply(req);

        return FacilitySeatPatchRes.builder()
                .seatId(seat.getId())
                .seatName(seat.getSeatName())
                .sortOrder(seat.getSortOrder())
                .isActive(seat.getIsActive())
                .updatedAt(seat.getUpdatedAt())
                .build();
    }

    // 입주민 시설 목록 조회
    public List<ResidentFacilityListRes> getResidentFacilityList(Long complexId, ResidentFacilityListReq req) {
        validateResidentAccess(complexId);

        Long typeId = req == null ? null : req.getTypeId();

        List<Facility> facilities = facilityRepository.findResidentFacilities(complexId, typeId);

        if (facilities.isEmpty()) {
            return List.of();
        }

        // 시설 정책 일괄 조회 (N+1 방지)
        List<Long> facilityIds = facilities.stream().map(Facility::getId).toList();
        java.util.Map<Long, FacilityPolicy> policyMap = facilityPolicyRepository
                .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        // 시설 타입 일괄 조회 (N+1 방지)
        List<Long> typeIds = facilities.stream().map(Facility::getTypeId).distinct().toList();
        java.util.Map<Long, FacilityType> typeMap = facilityTypeRepository.findAllById(typeIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(FacilityType::getId, t -> t));

        return facilities.stream()
                .map(facility -> {
                    FacilityPolicy policy = policyMap.get(facility.getId());
                    FacilityType type = typeMap.get(facility.getTypeId());
                    return ResidentFacilityListRes.builder()
                            .facilityId(facility.getId())
                            .name(facility.getName())
                            .typeId(facility.getTypeId())
                            .typeCode(type != null ? type.getTypeCode() : null)
                            .typeName(type != null ? type.getTypeName() : null)
                            .description(facility.getDescription())
                            .reservationType(facility.getReservationType())
                            .maxCount(policy == null ? null : policy.getMaxReservationCount())
                            .openTime(facility.getOpenTime())
                            .closeTime(facility.getCloseTime())
                            .isActive(facility.getIsActive())
                            .slotMin(policy == null ? null : policy.getSlotMin())
                            .baseFee(policy == null ? null : policy.getBaseFee())
                            .build();
                })
                .toList();
    }

    // 입주민 시설 상세 조회
    public ResidentFacilityDetailRes getResidentFacilityDetail(Long complexId, Long facilityId) {
        validateResidentAccess(complexId);

        Facility facility = getResidentFacility(complexId, facilityId);
        FacilityPolicy policy = facilityPolicyRepository
                .findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, facility.getId())
                .orElse(null);
        FacilityType type = facilityTypeRepository.findById(facility.getTypeId()).orElse(null);

        return ResidentFacilityDetailRes.builder()
                .facilityId(facility.getId())
                .typeId(facility.getTypeId())
                .typeCode(type != null ? type.getTypeCode() : null)
                .typeName(type != null ? type.getTypeName() : null)
                .name(facility.getName())
                .description(facility.getDescription())
                .reservationType(facility.getReservationType())
                .maxCount(policy == null ? null : policy.getMaxReservationCount())
                .openTime(facility.getOpenTime())
                .closeTime(facility.getCloseTime())
                .isActive(facility.getIsActive())
                .slotMin(policy == null ? null : policy.getSlotMin())
                .baseFee(policy == null ? null : policy.getBaseFee())
                .cancelDeadlineHours(policy == null ? null : policy.getCancelDeadlineHours())
                .feeType(policy == null ? null : policy.getFeeType())
                .subscribeCutoffDay(policy == null ? null : policy.getSubscribeCutoffDay())
                .cancelCutoffDay(policy == null ? null : policy.getCancelCutoffDay())
                .build();
    }

    // 시설 이용 현황 조회
    public FacilityUsageStatusRes getFacilityUsageStatus(Long complexId, FacilityUsageStatusReq req) {
        validateAdminAccess(complexId);

        if (req == null || req.getFacilityId() == null || req.getTargetDate() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        Facility facility = getFacility(complexId, req.getFacilityId());

        // 예약 상태별 이용 현황 집계
        int reservedCount = (int) reservationRepository.countByFacilityIdAndReservationDateAndStatus(
                facility.getId(), req.getTargetDate(), ReservationStatus.CONFIRMED);
        int completedCount = (int) reservationRepository.countByFacilityIdAndReservationDateAndStatus(
                facility.getId(), req.getTargetDate(), ReservationStatus.COMPLETED);
        int cancelledCount = (int) reservationRepository.countByFacilityIdAndReservationDateAndStatus(
                facility.getId(), req.getTargetDate(), ReservationStatus.CANCELLED);

        return FacilityUsageStatusRes.builder()
                .facilityId(facility.getId())
                .targetDate(req.getTargetDate())
                .reservedCount(reservedCount)
                .completedCount(completedCount)
                .cancelledCount(cancelledCount)
                .build();
    }

    // 관리자 좌석 상태 조회
    public List<SeatStatusRes> getSeatStatus(Long complexId, Long facilityId, SeatStatusReq req) {
        validateAdminAccess(complexId);
        if (req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateSlotRange(req.getTargetDate(), req.getStartTime(), req.getEndTime());

        Facility facility = getFacility(complexId, facilityId);
        if (facility.getReservationType() != ReservationType.SEAT) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        List<FacilitySeat> seats = facilitySeatRepository.findByFacilityIdAndIsActiveTrue(facilityId);
        List<FacilityBlockTime> blockTimes = facilityBlockTimeRepository
                .findByFacilityIdAndBlockDateAndIsActiveTrue(facilityId, req.getTargetDate());
        // 정기 휴무 규칙 조회
        List<FacilityClosureRule> closureRules = facilityClosureRuleRepository
                .findByFacilityIdAndIsActiveTrueOrderByCreatedAtDesc(facilityId);
        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatusIn(facilityId, req.getTargetDate(),
                        List.of(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED));
        List<ReservationTempHold> activeHolds = reservationTempHoldRepository
                .findByFacilityIdAndReservationDateAndHoldStatusAndExpiresAtAfter(
                        facilityId,
                        req.getTargetDate(),
                        ReservationHoldStatus.HOLDING,
                        LocalDateTime.now()
                );

        // 좌석 사용자 일괄 조회 (N+1 방지)
        Set<Long> userIds = java.util.stream.Stream.concat(
                        confirmedReservations.stream().map(Reservation::getUserId),
                        activeHolds.stream().map(ReservationTempHold::getUserId)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));

        Set<Long> seatHouseholdIds = confirmedReservations.stream()
                .map(Reservation::getHouseholdId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, HouseholdCache> seatHouseholdMap = seatHouseholdIds.isEmpty() ? Map.of() :
                householdCacheRepository.findAllById(seatHouseholdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getHouseholdId, h -> h));

        // 시설 전체 차단 여부 판별
        boolean facilityBlocked = blockTimes.stream()
                .filter(blockTime -> blockTime.getSeatId() == null)
                .anyMatch(blockTime -> isTimeOverlap(
                        req.getStartTime(),
                        req.getEndTime(),
                        blockTime.getStartTime(),
                        blockTime.getEndTime()
                ))
                || isBlockedByClosureRules(req.getTargetDate(), req.getStartTime(), req.getEndTime(), null, closureRules);

        return seats.stream()
                .map(seat -> {
                    // 좌석 차단 여부 판별
                    boolean seatBlockedByRule = isBlockedByClosureRules(
                            req.getTargetDate(), req.getStartTime(), req.getEndTime(), seat.getId(), closureRules);
                    if (facilityBlocked || isSeatBlocked(seat.getId(), blockTimes, req.getStartTime(), req.getEndTime())
                            || seatBlockedByRule) {
                        return SeatStatusRes.builder()
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("BLOCKED")
                                .residentName(null)
                                .holdExpiresAt(null)
                                .build();
                    }

                    Reservation reservedReservation = confirmedReservations.stream()
                            .filter(reservation -> seat.getId().equals(reservation.getSeatId()))
                            .filter(reservation -> isTimeOverlap(
                                    req.getStartTime(),
                                    req.getEndTime(),
                                    reservation.getStartTime(),
                                    reservation.getEndTime()
                            ))
                            .findFirst()
                            .orElse(null);
                    if (reservedReservation != null) {
                        UserCache user = userMap.get(reservedReservation.getUserId());
                        HouseholdCache household = seatHouseholdMap.get(reservedReservation.getHouseholdId());
                        String buildingNo = household != null ? household.getBuildingNo() : null;
                        String unitNo = household != null ? household.getUnitNo() : null;
                        String unit = (buildingNo != null && unitNo != null)
                                ? buildingNo + "동 " + unitNo + "호" : null;
                        return SeatStatusRes.builder()
                                .seatId(seat.getId())
                                .reservationId(String.valueOf(reservedReservation.getId()))
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("RESERVED")
                                .residentName(user != null ? user.getName() : null)
                                .dong(buildingNo)
                                .ho(unitNo)
                                .unit(unit)
                                .holdExpiresAt(null)
                                .build();
                    }

                    ReservationTempHold holding = activeHolds.stream()
                            .filter(hold -> seat.getId().equals(hold.getSeatId()))
                            .filter(hold -> isTimeOverlap(
                                    req.getStartTime(),
                                    req.getEndTime(),
                                    hold.getStartTime(),
                                    hold.getEndTime()
                            ))
                            .findFirst()
                            .orElse(null);
                    if (holding != null) {
                        UserCache user = userMap.get(holding.getUserId());
                        return SeatStatusRes.builder()
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("HOLDING")
                                .residentName(user != null ? user.getName() : null)
                                .holdExpiresAt(holding.getExpiresAt())
                                .build();
                    }

                    return SeatStatusRes.builder()
                            .seatId(seat.getId())
                            .seatNo(seat.getSeatNo())
                            .seatName(seat.getSeatName())
                            .status("AVAILABLE")
                            .residentName(null)
                            .holdExpiresAt(null)
                            .build();
                })
                .toList();
    }

    // 정원형 이용 현황 조회
    public CountStatusRes getCountStatus(Long complexId, Long facilityId, CountStatusReq req) {
        validateAdminAccess(complexId);
        if (req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateSlotRange(req.getTargetDate(), req.getStartTime(), req.getEndTime());

        Facility facility = getFacility(complexId, facilityId);
        if (facility.getReservationType() != ReservationType.COUNT) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        FacilityPolicy policy = facilityPolicyRepository.findByComplexIdAndFacilityIdAndIsActiveTrue(complexId, facilityId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY));
        if (policy.getMaxReservationCount() == null || policy.getMaxReservationCount() <= 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        // 정기 휴무 슬롯 차단
        List<FacilityClosureRule> closureRules = facilityClosureRuleRepository
                .findByFacilityIdAndIsActiveTrueOrderByCreatedAtDesc(facilityId);
        boolean blockedByRule = isBlockedByClosureRules(
                req.getTargetDate(), req.getStartTime(), req.getEndTime(), null, closureRules);

        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatusIn(facilityId, req.getTargetDate(),
                        List.of(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED))
                .stream()
                .filter(reservation -> req.getStartTime().equals(reservation.getStartTime())
                        && req.getEndTime().equals(reservation.getEndTime()))
                .toList();

        // COUNT 선점 미지원
        // 정기 휴무 잔여 수량 차단
        int reservedCount = confirmedReservations.size();
        int maxCount = policy.getMaxReservationCount();
        int availableCount = blockedByRule ? 0 : Math.max(0, maxCount - reservedCount);

        Set<Long> userIds = confirmedReservations.stream()
                .map(Reservation::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));

        // 세대 정보 일괄 조회 (N+1 방지, 누락 캐시 null 허용)
        Set<Long> householdIds = confirmedReservations.stream()
                .map(Reservation::getHouseholdId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, HouseholdCache> householdMap = householdIds.isEmpty() ? Map.of() :
                householdCacheRepository.findAllById(householdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getHouseholdId, h -> h));

        return CountStatusRes.builder()
                .facilityId(facilityId)
                .targetDate(req.getTargetDate())
                .maxCount(maxCount)
                .reservedCount(reservedCount)
                .availableCount(availableCount)
                .users(confirmedReservations.stream()
                        .map(reservation -> {
                            UserCache user = userMap.get(reservation.getUserId());
                            HouseholdCache household = householdMap.get(reservation.getHouseholdId());
                            String buildingNo = household != null ? household.getBuildingNo() : null;
                            String unitNo = household != null ? household.getUnitNo() : null;
                            String unit = (buildingNo != null && unitNo != null)
                                    ? buildingNo + "동 " + unitNo + "호" : null;
                            return CountStatusRes.UserItem.builder()
                                    .reservationId(String.valueOf(reservation.getId()))
                                    .residentName(user != null ? user.getName() : null)
                                    .dong(buildingNo)
                                    .ho(unitNo)
                                    .unit(unit)
                                    .status(reservation.getStatus().name())
                                    .build();
                        })
                        .toList())
                .build();
    }

    // 입주민 좌석 상태 조회 (개인정보 제외)
    public List<ResidentSeatStatusRes> getResidentSeatStatus(Long complexId, Long facilityId, SeatStatusReq req) {
        validateResidentAccess(complexId);
        if (req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateSlotRange(req.getTargetDate(), req.getStartTime(), req.getEndTime());

        // 활성 시설 검증
        Facility facility = getResidentFacility(complexId, facilityId);
        if (facility.getReservationType() != ReservationType.SEAT) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        List<FacilitySeat> seats = facilitySeatRepository.findByFacilityIdAndIsActiveTrue(facilityId);
        List<FacilityBlockTime> blockTimes = facilityBlockTimeRepository
                .findByFacilityIdAndBlockDateAndIsActiveTrue(facilityId, req.getTargetDate());
        // 정기 휴무 규칙 조회
        List<FacilityClosureRule> closureRules = facilityClosureRuleRepository
                .findByFacilityIdAndIsActiveTrueOrderByCreatedAtDesc(facilityId);
        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatus(facilityId, req.getTargetDate(), ReservationStatus.CONFIRMED);
        List<ReservationTempHold> activeHolds = reservationTempHoldRepository
                .findByFacilityIdAndReservationDateAndHoldStatusAndExpiresAtAfter(
                        facilityId,
                        req.getTargetDate(),
                        ReservationHoldStatus.HOLDING,
                        LocalDateTime.now()
                );

        // 시설 전체 차단 여부 판별
        boolean facilityBlocked = blockTimes.stream()
                .filter(blockTime -> blockTime.getSeatId() == null)
                .anyMatch(blockTime -> isTimeOverlap(
                        req.getStartTime(),
                        req.getEndTime(),
                        blockTime.getStartTime(),
                        blockTime.getEndTime()
                ))
                || isBlockedByClosureRules(req.getTargetDate(), req.getStartTime(), req.getEndTime(), null, closureRules);

        return seats.stream()
                .map(seat -> {
                    boolean seatBlockedByRule = isBlockedByClosureRules(
                            req.getTargetDate(), req.getStartTime(), req.getEndTime(), seat.getId(), closureRules);
                    if (facilityBlocked || isSeatBlocked(seat.getId(), blockTimes, req.getStartTime(), req.getEndTime())
                            || seatBlockedByRule) {
                        return ResidentSeatStatusRes.builder()
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("BLOCKED")
                                .holdExpiresAt(null)
                                .build();
                    }

                    boolean isReserved = confirmedReservations.stream()
                            .filter(reservation -> seat.getId().equals(reservation.getSeatId()))
                            .anyMatch(reservation -> isTimeOverlap(
                                    req.getStartTime(),
                                    req.getEndTime(),
                                    reservation.getStartTime(),
                                    reservation.getEndTime()
                            ));
                    if (isReserved) {
                        return ResidentSeatStatusRes.builder()
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("RESERVED")
                                .holdExpiresAt(null)
                                .build();
                    }

                    ReservationTempHold holding = activeHolds.stream()
                            .filter(hold -> seat.getId().equals(hold.getSeatId()))
                            .filter(hold -> isTimeOverlap(
                                    req.getStartTime(),
                                    req.getEndTime(),
                                    hold.getStartTime(),
                                    hold.getEndTime()
                            ))
                            .findFirst()
                            .orElse(null);
                    if (holding != null) {
                        return ResidentSeatStatusRes.builder()
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("HOLDING")
                                .holdExpiresAt(holding.getExpiresAt())
                                .build();
                    }

                    return ResidentSeatStatusRes.builder()
                            .seatId(seat.getId())
                            .seatNo(seat.getSeatNo())
                            .seatName(seat.getSeatName())
                            .status("AVAILABLE")
                            .holdExpiresAt(null)
                            .build();
                })
                .toList();
    }

    // 좌석별 차단 여부 판별
    private boolean isSeatBlocked(Long seatId, List<FacilityBlockTime> blockTimes, LocalTime startTime, LocalTime endTime) {
        return blockTimes.stream()
                .filter(blockTime -> seatId.equals(blockTime.getSeatId()))
                .anyMatch(blockTime -> isTimeOverlap(startTime, endTime, blockTime.getStartTime(), blockTime.getEndTime()));
    }

    // 정기 휴무 차단 여부 판별 (시설/좌석)
    private boolean isBlockedByClosureRules(LocalDate date, LocalTime startTime, LocalTime endTime,
                                             Long seatId, List<FacilityClosureRule> rules) {
        return rules.stream()
                // 날짜 패턴 필터
                .filter(rule -> rule.isDateBlocked(date))
                // 시설/좌석 적용 범위 필터
                .filter(rule -> rule.getSeatId() == null || rule.getSeatId().equals(seatId))
                // 종일/시간대 차단 판별
                .anyMatch(rule -> rule.isAllDay()
                        || isTimeOverlap(startTime, endTime, rule.getStartTime(), rule.getEndTime()));
    }

    // 정기 휴무 규칙 요청 검증
    private void validateClosureRulePostReq(FacilityClosureRulePostReq req) {
        if (req == null || req.getRuleType() == null
                || req.getDaysOfWeek() == null || req.getDaysOfWeek().isEmpty()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        // MONTHLY_NTH 주차 필수 검증
        if (req.getRuleType() == com.apten.facilityreservation.domain.enums.ClosureRuleType.MONTHLY_NTH
                && (req.getWeekOrdinals() == null || req.getWeekOrdinals().isEmpty())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        // 주차 범위 검증 (1~5주)
        if (req.getWeekOrdinals() != null) {
            for (int ordinal : req.getWeekOrdinals()) {
                if (ordinal < 1 || ordinal > 5) throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
        }
        // 시작/종료 시각 쌍 검증
        boolean hasStart = req.getStartTime() != null;
        boolean hasEnd = req.getEndTime() != null;
        if (hasStart != hasEnd || (hasStart && !req.getStartTime().isBefore(req.getEndTime()))) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        // 적용 기간 순서 검증
        if (req.getValidFrom() != null && req.getValidUntil() != null
                && req.getValidFrom().isAfter(req.getValidUntil())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 요일 목록 저장값 변환
    private String joinDaysOfWeek(List<java.time.DayOfWeek> days) {
        return days.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    // 주차 목록 저장값 변환
    private String joinWeekOrdinals(List<Integer> ordinals) {
        if (ordinals == null || ordinals.isEmpty()) return null;
        return ordinals.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    // 정기 휴무 규칙 등록
    @Transactional
    public FacilityClosureRulePostRes createClosureRule(Long complexId, Long facilityId, FacilityClosureRulePostReq req) {
        // 시설 접근/소속 검증
        validateAdminAccess(complexId);
        Facility facility = getFacility(complexId, facilityId);

        // 요청값 검증
        validateClosureRulePostReq(req);

        // 좌석 소속 검증 (seatId 지정 시)
        if (req.getSeatId() != null) {
            FacilitySeat seat = facilitySeatRepository.findById(req.getSeatId())
                    .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));
            if (!seat.getFacilityId().equals(facility.getId())) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
        }

        // 규칙 저장
        FacilityClosureRule rule = facilityClosureRuleRepository.save(FacilityClosureRule.builder()
                .facilityId(facility.getId())
                .seatId(req.getSeatId())
                .ruleType(req.getRuleType())
                .daysOfWeek(joinDaysOfWeek(req.getDaysOfWeek()))
                .weekOrdinals(joinWeekOrdinals(req.getWeekOrdinals()))
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .validFrom(req.getValidFrom())
                .validUntil(req.getValidUntil())
                .reason(req.getReason())
                .build());

        return FacilityClosureRulePostRes.builder()
                .closureRuleId(rule.getId())
                .facilityId(rule.getFacilityId())
                .ruleType(rule.getRuleType())
                .daysOfWeek(req.getDaysOfWeek())
                .weekOrdinals(req.getWeekOrdinals())
                .startTime(rule.getStartTime())
                .endTime(rule.getEndTime())
                .validFrom(rule.getValidFrom())
                .validUntil(rule.getValidUntil())
                .createdAt(rule.getCreatedAt())
                .build();
    }

    // 정기 휴무 규칙 목록 조회
    public List<FacilityClosureRuleListRes> getClosureRuleList(Long complexId, Long facilityId) {
        // 시설 접근/소속 검증
        validateAdminAccess(complexId);
        Facility facility = getFacility(complexId, facilityId);

        // 연관 좌석 일괄 조회 (N+1 방지)
        List<FacilityClosureRule> rules = facilityClosureRuleRepository
                .findByFacilityIdAndIsActiveTrueOrderByCreatedAtDesc(facility.getId());

        Set<Long> seatIds = rules.stream()
                .map(FacilityClosureRule::getSeatId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, FacilitySeat> seatMap = seatIds.isEmpty() ? Map.of()
                : facilitySeatRepository.findAllById(seatIds).stream()
                        .collect(Collectors.toMap(FacilitySeat::getId, s -> s));

        return rules.stream()
                .map(rule -> {
                    FacilitySeat seat = rule.getSeatId() != null ? seatMap.get(rule.getSeatId()) : null;
                    return FacilityClosureRuleListRes.builder()
                            .closureRuleId(rule.getId())
                            .facilityId(rule.getFacilityId())
                            .seatId(rule.getSeatId())
                            .seatNo(seat != null ? seat.getSeatNo() : null)
                            .seatName(seat != null ? seat.getSeatName() : null)
                            .ruleType(rule.getRuleType())
                            .ruleTypeLabel(rule.getRuleType().getLabel())
                            .daysOfWeek(new java.util.ArrayList<>(rule.parseDaysOfWeek()))
                            .weekOrdinals(new java.util.ArrayList<>(rule.parseWeekOrdinals()))
                            .startTime(rule.getStartTime())
                            .endTime(rule.getEndTime())
                            .validFrom(rule.getValidFrom())
                            .validUntil(rule.getValidUntil())
                            .reason(rule.getReason())
                            .isActive(rule.getIsActive())
                            .build();
                })
                .toList();
    }

    // 정기 휴무 규칙 수정
    @Transactional
    public FacilityClosureRuleListRes updateClosureRule(Long complexId, Long facilityId,
                                                         Long ruleId, FacilityClosureRulePatchReq req) {
        // 시설 접근/소속 검증
        validateAdminAccess(complexId);
        getFacility(complexId, facilityId);

        // 규칙/시설 소속 검증
        FacilityClosureRule rule = facilityClosureRuleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));
        if (!rule.getFacilityId().equals(facilityId)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 시작/종료 시각 쌍 검증
        boolean hasStart = req.getStartTime() != null;
        boolean hasEnd = req.getEndTime() != null;
        if (hasStart != hasEnd || (hasStart && !req.getStartTime().isBefore(req.getEndTime()))) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 규칙 내용 업데이트
        rule.update(
                req.getRuleType(),
                joinDaysOfWeek(req.getDaysOfWeek()),
                joinWeekOrdinals(req.getWeekOrdinals()),
                req.getStartTime(),
                req.getEndTime(),
                req.getValidFrom(),
                req.getValidUntil(),
                req.getReason()
        );

        return FacilityClosureRuleListRes.builder()
                .closureRuleId(rule.getId())
                .facilityId(rule.getFacilityId())
                .ruleType(rule.getRuleType())
                .ruleTypeLabel(rule.getRuleType().getLabel())
                .daysOfWeek(new java.util.ArrayList<>(rule.parseDaysOfWeek()))
                .weekOrdinals(new java.util.ArrayList<>(rule.parseWeekOrdinals()))
                .startTime(rule.getStartTime())
                .endTime(rule.getEndTime())
                .validFrom(rule.getValidFrom())
                .validUntil(rule.getValidUntil())
                .reason(rule.getReason())
                .isActive(rule.getIsActive())
                .build();
    }

    // 정기 휴무 규칙 비활성화
    @Transactional
    public FacilityClosureRuleDeactivateRes deactivateClosureRule(Long complexId, Long facilityId, Long ruleId) {
        // 시설 접근/소속 검증
        validateAdminAccess(complexId);
        getFacility(complexId, facilityId);

        // 규칙/시설 소속 검증
        FacilityClosureRule rule = facilityClosureRuleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));
        if (!rule.getFacilityId().equals(facilityId)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        rule.deactivate();

        return FacilityClosureRuleDeactivateRes.builder()
                .closureRuleId(rule.getId())
                .processedAt(LocalDateTime.now())
                .build();
    }
}
