package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.facilityreservation.application.model.request.CountStatusReq;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeListReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityListReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPatchReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatBulkPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPostReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityTypeListReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePostReq;
import com.apten.facilityreservation.application.model.request.FacilityUsageStatusReq;
import com.apten.facilityreservation.application.model.request.ResidentFacilityListReq;
import com.apten.facilityreservation.application.model.request.SeatStatusReq;
import com.apten.facilityreservation.application.model.response.CountStatusRes;
import com.apten.facilityreservation.application.model.response.FacilityActivePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeListRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePostRes;
import com.apten.facilityreservation.application.model.response.FacilityDeleteRes;
import com.apten.facilityreservation.application.model.response.FacilityDetailRes;
import com.apten.facilityreservation.application.model.response.FacilityListRes;
import com.apten.facilityreservation.application.model.response.FacilityPatchRes;
import com.apten.facilityreservation.application.model.response.FacilityPostRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatListRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPatchRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatBulkPostRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPostRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeListRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePostRes;
import com.apten.facilityreservation.application.model.response.FacilityUsageStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ResidentFacilityDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilityListRes;
import com.apten.facilityreservation.application.model.response.ResidentSeatStatusRes;
import com.apten.facilityreservation.application.model.response.SeatStatusRes;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설과 시설 타입, 좌석, 차단 시간 관련 API
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

    // 입주민이 조회 가능한 활성 시설을 조회한다.
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

    // 운영 시간 검증
    private void validateTime(java.time.LocalTime openTime, java.time.LocalTime closeTime) {
        if (openTime == null || closeTime == null || !openTime.isBefore(closeTime)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 현황 조회는 슬롯 범위 해석이 핵심이므로 시작/종료 시각이 모두 필요하다.
    private void validateSlotRange(LocalDate targetDate, LocalTime startTime, LocalTime endTime) {
        if (targetDate == null || startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 차단과 예약은 같은 시간 구간을 해석해야 하므로 공통 겹침 규칙을 재사용한다.
    private boolean isTimeOverlap(LocalTime slotStart, LocalTime slotEnd, LocalTime candidateStart, LocalTime candidateEnd) {
        LocalTime effectiveStart = candidateStart != null ? candidateStart : LocalTime.MIN;
        LocalTime effectiveEnd = candidateEnd != null ? candidateEnd : LocalTime.MAX;
        return slotStart.isBefore(effectiveEnd) && slotEnd.isAfter(effectiveStart);
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

    // 정책 기준 예약 단위 표시 라벨을 생성한다.
    // MINUTE: slotMin값+"분" (예: "60분"), DAY: "하루", 정책 없으면 null
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

        // 정책 배치 조회 (N+1 방지)
        Map<Long, FacilityPolicy> policyMap = facilityIds.isEmpty() ? Map.of() :
                facilityPolicyRepository.findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                        .stream()
                        .collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        // 목록 응답 변환
        List<FacilityListRes> items = facilities.stream()
                .map(facility -> {
                    FacilityPolicy policy = policyMap.get(facility.getId());
                    return FacilityListRes.builder()
                            .facilityId(facility.getId())
                            .typeId(facility.getTypeId())
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

    // 관리자 시설 상세를 조회한다. API-603
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

    // 관리자 시설 삭제를 처리한다. API-605
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

//    // 시설 타입을 등록한다. 부트스트랩처리
//    public FacilityTypePostRes createFacilityType(Long complexId, FacilityTypePostReq req) {
//        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
//        return FacilityTypePostRes.builder()
//                .facilityTypeId(0L)
//                .typeCode(req.getTypeCode())
//                .typeName(req.getTypeName())
//                .createdAt(LocalDateTime.now())
//                .build();
//    }

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
        return facilityBlockTimeRepository.findBlockTimes(
                        facility.getId(),
                        fromDate,
                        toDate,
                        isActive
                )
                .stream()
                .map(blockTime -> FacilityBlockTimeListRes.builder()
                        .facilityBlockTimeId(blockTime.getId())
                        .facilityId(blockTime.getFacilityId())
                        .seatId(blockTime.getSeatId())
                        .blockDate(blockTime.getBlockDate())
                        .startTime(blockTime.getStartTime())
                        .endTime(blockTime.getEndTime())
                        .reason(blockTime.getReason())
                        .isActive(blockTime.getIsActive())
                        .build())
                .toList();
    }

    // 시설 좌석을 등록한다. API-614
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

    // 좌석 라벨 규칙은 빠르게 여러 좌석을 구분하려는 운영 목적을 따른다.
    private String resolveSeatLabel(String prefix, Integer seatNo) {
        return prefix + "-" + seatNo;
    }

    // 패턴이 없을 때도 운영자가 구간 라벨을 바로 알아볼 수 있게 기본 좌석명을 맞춘다.
    private String resolveSeatName(String seatNamePattern, String label, Integer seatNo) {
        String effectivePattern = seatNamePattern == null || seatNamePattern.isBlank()
                ? "{label} 좌석"
                : seatNamePattern;
        return effectivePattern
                .replace("{seatNo}", String.valueOf(seatNo))
                .replace("{label}", label);
    }

    // 시설 좌석을 일괄 등록한다.
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

        // 하나라도 겹치면 전부 실패시켜야 좌석 구간 생성 결과가 반쪽만 남지 않는다.
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

    // 시설 좌석 목록을 조회한다. API-615
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

    // 시설 좌석을 수정한다. API-616
    @Transactional
    public FacilitySeatPatchRes updateFacilitySeat(Long complexId, Long seatId, FacilitySeatPatchReq req) {
        validateAdminAccess(complexId);

        FacilitySeat seat = facilitySeatRepository.findById(seatId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_SEAT_NOT_FOUND));

        // 해당 좌석이 현재 complexId 소속 시설인지 검증한다.
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

    // 입주민 시설 목록을 조회한다. API-617
    public List<ResidentFacilityListRes> getResidentFacilityList(Long complexId, ResidentFacilityListReq req) {
        validateResidentAccess(complexId);

        Long typeId = req == null ? null : req.getTypeId();

        List<Facility> facilities = facilityRepository.findResidentFacilities(complexId, typeId);

        if (facilities.isEmpty()) {
            return List.of();
        }

        // 시설 ID 목록으로 정책을 일괄 조회해 N+1을 제거한다.
        List<Long> facilityIds = facilities.stream().map(Facility::getId).toList();
        java.util.Map<Long, FacilityPolicy> policyMap = facilityPolicyRepository
                .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        // 타입 ID 목록으로 시설 타입을 일괄 조회해 N+1을 제거한다.
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

    // 입주민 시설 상세를 조회한다. API-618
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
                .build();
    }

    // 시설 이용 현황을 조회한다. API-644
    public FacilityUsageStatusRes getFacilityUsageStatus(Long complexId, FacilityUsageStatusReq req) {
        validateAdminAccess(complexId);

        if (req == null || req.getFacilityId() == null || req.getTargetDate() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        Facility facility = getFacility(complexId, req.getFacilityId());

        // 상태별 건수는 오늘 이용 현황 요약이므로 HOLDING이 아닌 예약 상태만 집계한다.
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

    // 좌석 상태를 조회한다. API-645
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
        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatus(facilityId, req.getTargetDate(), ReservationStatus.CONFIRMED);
        List<ReservationTempHold> activeHolds = reservationTempHoldRepository
                .findByFacilityIdAndReservationDateAndHoldStatusAndExpiresAtAfter(
                        facilityId,
                        req.getTargetDate(),
                        ReservationHoldStatus.HOLDING,
                        LocalDateTime.now()
                );

        // 좌석 표시 이름은 예약/선점 사용자 이름이 있으면 보여주므로 user_cache를 한 번에 읽는다.
        Set<Long> userIds = java.util.stream.Stream.concat(
                        confirmedReservations.stream().map(Reservation::getUserId),
                        activeHolds.stream().map(ReservationTempHold::getUserId)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));

        // 시설 전체 차단이 겹치면 모든 좌석을 BLOCKED로 본다.
        boolean facilityBlocked = blockTimes.stream()
                .filter(blockTime -> blockTime.getSeatId() == null)
                .anyMatch(blockTime -> isTimeOverlap(
                        req.getStartTime(),
                        req.getEndTime(),
                        blockTime.getStartTime(),
                        blockTime.getEndTime()
                ));

        return seats.stream()
                .map(seat -> {
                    if (facilityBlocked || isSeatBlocked(seat.getId(), blockTimes, req.getStartTime(), req.getEndTime())) {
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
                        return SeatStatusRes.builder()
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .seatName(seat.getSeatName())
                                .status("RESERVED")
                                .residentName(user != null ? user.getName() : null)
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

    // 정원형 이용 현황을 조회한다. API-646
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

        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatus(facilityId, req.getTargetDate(), ReservationStatus.CONFIRMED)
                .stream()
                .filter(reservation -> req.getStartTime().equals(reservation.getStartTime())
                        && req.getEndTime().equals(reservation.getEndTime()))
                .toList();

        // COUNT용 HOLDING은 아직 생성하지 않으므로 현재 잔여 수량은 CONFIRMED 기준으로만 계산한다.
        int reservedCount = confirmedReservations.size();
        int maxCount = policy.getMaxReservationCount();
        int availableCount = Math.max(0, maxCount - reservedCount);

        Set<Long> userIds = confirmedReservations.stream()
                .map(Reservation::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of() :
                userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, u -> u));

        // 동/호수 표시를 위해 HouseholdCache를 batch 조회한다. 캐시 미동기 시 null로 내려간다.
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
                                    .reservationId(reservation.getId())
                                    .residentName(user != null ? user.getName() : null)
                                    .dong(buildingNo)
                                    .ho(unitNo)
                                    .unit(unit)
                                    .build();
                        })
                        .toList())
                .build();
    }

    // 입주민 좌석 상태를 조회한다. 다른 입주민 개인정보는 응답에 포함하지 않는다.
    public List<ResidentSeatStatusRes> getResidentSeatStatus(Long complexId, Long facilityId, SeatStatusReq req) {
        validateResidentAccess(complexId);
        if (req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateSlotRange(req.getTargetDate(), req.getStartTime(), req.getEndTime());

        // 활성 시설인지 함께 검증한다.
        Facility facility = getResidentFacility(complexId, facilityId);
        if (facility.getReservationType() != ReservationType.SEAT) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        List<FacilitySeat> seats = facilitySeatRepository.findByFacilityIdAndIsActiveTrue(facilityId);
        List<FacilityBlockTime> blockTimes = facilityBlockTimeRepository
                .findByFacilityIdAndBlockDateAndIsActiveTrue(facilityId, req.getTargetDate());
        List<Reservation> confirmedReservations = reservationRepository
                .findByFacilityIdAndReservationDateAndStatus(facilityId, req.getTargetDate(), ReservationStatus.CONFIRMED);
        List<ReservationTempHold> activeHolds = reservationTempHoldRepository
                .findByFacilityIdAndReservationDateAndHoldStatusAndExpiresAtAfter(
                        facilityId,
                        req.getTargetDate(),
                        ReservationHoldStatus.HOLDING,
                        LocalDateTime.now()
                );

        // 시설 전체 차단 여부를 먼저 판단한다.
        boolean facilityBlocked = blockTimes.stream()
                .filter(blockTime -> blockTime.getSeatId() == null)
                .anyMatch(blockTime -> isTimeOverlap(
                        req.getStartTime(),
                        req.getEndTime(),
                        blockTime.getStartTime(),
                        blockTime.getEndTime()
                ));

        return seats.stream()
                .map(seat -> {
                    if (facilityBlocked || isSeatBlocked(seat.getId(), blockTimes, req.getStartTime(), req.getEndTime())) {
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

    // 좌석 차단은 좌석별 설비 이슈도 포함하므로 시설 전체 차단과 분리해 판단한다.
    private boolean isSeatBlocked(Long seatId, List<FacilityBlockTime> blockTimes, LocalTime startTime, LocalTime endTime) {
        return blockTimes.stream()
                .filter(blockTime -> seatId.equals(blockTime.getSeatId()))
                .anyMatch(blockTime -> isTimeOverlap(startTime, endTime, blockTime.getStartTime(), blockTime.getEndTime()));
    }
}
