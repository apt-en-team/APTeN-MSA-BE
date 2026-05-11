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
import com.apten.facilityreservation.application.model.response.FacilitySeatPostRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeListRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePostRes;
import com.apten.facilityreservation.application.model.response.FacilityUsageStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ResidentFacilityDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilityListRes;
import com.apten.facilityreservation.application.model.response.SeatStatusRes;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import com.apten.facilityreservation.domain.enums.ReservationType;
import com.apten.facilityreservation.domain.repository.*;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final FacilitySeatRepository facilitySeatRepository;
    private final ReservationRepository reservationRepository;
    private final ComplexCacheRepository complexCacheRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FacilityBlockTimeRepository facilityBlockTimeRepository;


    // 시설 관리자 접근 검증
    private void validateAdminAccess(Long complexId) {
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
    }

    // 시설 수정 요청 검증
    private void validatePatchReq(FacilityPatchReq req) {
        if (req == null || req.getName() == null || req.getName().isBlank()
                || req.getTypeId() == null || req.getReservationType() == null
                || req.getOpenTime() == null || req.getCloseTime() == null) {
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

    // 예약 정책값 검증
    private void validateRule(ReservationType reservationType, Integer maxCount, Integer slotMin, BigDecimal baseFee) {
        if (reservationType == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (slotMin == null || slotMin <= 0 || baseFee == null || baseFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        if ((reservationType == ReservationType.COUNT || reservationType == ReservationType.APPROVAL)
                && (maxCount == null || maxCount <= 0)) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }
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

    // 관리자 시설 등록
    @Transactional
    public FacilityPostRes createFacility(Long complexId, FacilityPostReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 등록 요청 필수값 검증
        validateCreateReq(req);

        // 시설 타입 존재 검증
        FacilityType facilityType = getFacilityType(req.getTypeId());

        // 시설 타입별 기본 정책 조회
        Optional<FacilityPolicy> policy =
                facilityPolicyRepository.findByComplexIdAndFacilityTypeCodeAndIsActiveTrue(
                        complexId,
                        facilityType.getTypeCode()
                );

        // 예약 방식 기본값 처리
        ReservationType reservationType = req.getReservationType() != null
                ? req.getReservationType()
                : ReservationType.COUNT;

        // 예약 단위 기본값 처리
        Integer slotMin = req.getSlotMin() != null
                ? req.getSlotMin()
                : policy.map(FacilityPolicy::getSlotMin).orElse(30);

        // 기본 요금 기본값 처리
        BigDecimal baseFee = req.getBaseFee() != null
                ? req.getBaseFee()
                : policy.map(FacilityPolicy::getBaseFee).orElse(BigDecimal.ZERO);

        // 운영 시간 검증
        validateTime(req.getOpenTime(), req.getCloseTime());

        // 예약 정책값 검증
        validateRule(reservationType, req.getMaxCount(), slotMin, baseFee);

        // 시설 엔티티 생성 및 저장
        Facility savedFacility = facilityRepository.save(Facility.builder()
                .complexId(complexId)
                .typeId(req.getTypeId())
                .name(req.getName())
                .description(req.getDescription())
                .reservationType(reservationType)
                .maxCount(req.getMaxCount())
                .openTime(req.getOpenTime())
                .closeTime(req.getCloseTime())
                .slotMin(slotMin)
                .baseFee(baseFee)
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

    // 관리자 시설 목록을 조회한다. API-602
    public PageResponse<FacilityListRes> getAdminFacilityList(Long complexId, FacilityListReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 페이지 번호 보정
        int page = Math.max(req.getPage(), 0);

        // 페이지 크기 보정
        int size = req.getSize() > 0 ? req.getSize() : 20;

        // 관리자 시설 목록 조회
        Page<FacilityListRes> facilityPage = facilityRepository
                .findAdminFacilities(complexId, req.getTypeId(), req.getReservationType(), req.getIsActive(), PageRequest.of(page, size))
                .map(facility -> FacilityListRes.builder()
                .facilityId(facility.getId())
                .typeId(facility.getTypeId())
                .name(facility.getName())
                .reservationType(facility.getReservationType())
                .maxCount(facility.getMaxCount())
                .openTime(facility.getOpenTime())
                .closeTime(facility.getCloseTime())
                .isActive(facility.getIsActive())
                .build());

        // 페이지 응답 변환
        return toPageResponse(facilityPage);
    }

    // 관리자 시설 상세를 조회한다. API-603
    public FacilityDetailRes getAdminFacilityDetail(Long complexId, Long facilityId) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 시설 소속 및 삭제 여부 검증
        Facility facility = getFacility(complexId, facilityId);

        // 시설 타입 정보 조회
        FacilityType facilityType = getFacilityType(facility.getTypeId());

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
                .maxCount(facility.getMaxCount())
                .openTime(facility.getOpenTime())
                .closeTime(facility.getCloseTime())
                .slotMin(facility.getSlotMin())
                .baseFee(facility.getBaseFee())
                .isActive(facility.getIsActive())
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

        // 예약 정책값 검증
        validateRule(req.getReservationType(), req.getMaxCount(), req.getSlotMin(), req.getBaseFee());

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

        // 차단 시간 저장
        FacilityBlockTime blockTime = facilityBlockTimeRepository.save(FacilityBlockTime.builder()
                .facilityId(facility.getId())
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
                        .blockDate(blockTime.getBlockDate())
                        .startTime(blockTime.getStartTime())
                        .endTime(blockTime.getEndTime())
                        .reason(blockTime.getReason())
                        .isActive(blockTime.getIsActive())
                        .build())
                .toList();
    }

    // 시설 좌석을 등록한다. API-614
    public FacilitySeatPostRes createFacilitySeat(Long complexId, Long facilityId, FacilitySeatPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) facility가 SEAT 예약 방식인지 확인한다.
        // 4) seatNo 중복 여부를 검증한다.
        // 5) 좌석 저장 및 응답 DTO 변환을 수행한다.
        return FacilitySeatPostRes.builder()
                .seatId(0L)
                .facilityId(facilityId)
                .seatNo(req.getSeatNo())
                .seatName(req.getSeatName())
                .isActive(req.getIsActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 시설 좌석 목록을 조회한다. API-615
    public List<FacilitySeatListRes> getFacilitySeatList(Long complexId, Long facilityId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) 시설별 좌석 목록을 조회한다.
        return List.of();
    }

    // 시설 좌석을 수정한다. API-616
    public FacilitySeatPatchRes updateFacilitySeat(Long complexId, Long seatId, FacilitySeatPatchReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) seatId가 현재 complexId 소속 시설의 좌석인지 검증한다.
        // 3) seatName, sortOrder, isActive 수정 가능 여부를 검증한다.
        // 4) Entity 저장 및 응답 DTO 변환을 수행한다.
        return FacilitySeatPatchRes.builder()
                .seatId(seatId)
                .seatName(req.getSeatName())
                .sortOrder(req.getSortOrder())
                .isActive(req.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 입주민 시설 목록을 조회한다. API-617
    public List<ResidentFacilityListRes> getResidentFacilityList(Long complexId, ResidentFacilityListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) 입주민 단지 컨텍스트 complexId 기준으로 활성 시설 목록을 조회한다.
        // 3) typeId 필터를 적용한다.
        // 4) 시설 정책 기본값과 시설 override 값을 합쳐 응답 DTO를 구성한다.
        return List.of();
    }

    // 입주민 시설 상세를 조회한다. API-618
    public ResidentFacilityDetailRes getResidentFacilityDetail(Long complexId, Long facilityId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) 시설 활성 상태를 확인한다.
        // 4) facility_policy와 facility override를 합쳐 slotMin, baseFee, cancelDeadlineHours를 계산한다.
        return ResidentFacilityDetailRes.builder().facilityId(facilityId).build();
    }

    // 시설 이용 현황을 조회한다. API-644
    public FacilityUsageStatusRes getFacilityUsageStatus(Long complexId, FacilityUsageStatusReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) targetDate 기준 reserved/completed/cancelled 집계를 계산한다.
        return FacilityUsageStatusRes.builder()
                .facilityId(req.getFacilityId())
                .targetDate(req.getTargetDate())
                .reservedCount(0)
                .completedCount(0)
                .cancelledCount(0)
                .build();
    }

    // 좌석 상태를 조회한다. API-645
    public List<SeatStatusRes> getSeatStatus(Long complexId, Long facilityId, SeatStatusReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) 좌석형 시설인지 확인한다.
        // 4) targetDate/startTime/endTime 기준 좌석 상태를 계산한다.
        // 5) Redis TEMP_HOLD와 reservation_temp_hold 상태를 함께 해석하도록 2단계에서 확장한다.
        return List.of();
    }

    // 정원형 이용 현황을 조회한다. API-646
    public CountStatusRes getCountStatus(Long complexId, Long facilityId, CountStatusReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) 정원형 시설인지 확인한다.
        // 4) targetDate/startTime/endTime 기준 예약 수와 잔여 정원을 계산한다.
        // 5) 사용자 목록은 reservation + user_cache를 조합해 구성한다.
        return CountStatusRes.builder()
                .facilityId(facilityId)
                .targetDate(req.getTargetDate())
                .maxCount(0)
                .reservedCount(0)
                .availableCount(0)
                .users(List.of())
                .build();
    }
}
