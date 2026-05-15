package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.GxBulkApproveReq;
import com.apten.facilityreservation.application.model.request.GxProgramCancelReq;
import com.apten.facilityreservation.application.model.request.GxProgramListReq;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.application.model.request.GxProgramPostReq;
import com.apten.facilityreservation.application.model.request.ResidentGxProgramListReq;
import com.apten.facilityreservation.application.model.response.GxBulkApproveRes;
import com.apten.facilityreservation.application.model.response.GxMinimumCheckRes;
import com.apten.facilityreservation.application.model.response.GxProgramCancelRes;
import com.apten.facilityreservation.application.model.response.GxProgramDetailRes;
import com.apten.facilityreservation.application.model.response.GxProgramListRes;
import com.apten.facilityreservation.application.model.response.GxProgramPatchRes;
import com.apten.facilityreservation.application.model.response.GxProgramPostRes;
import com.apten.facilityreservation.application.model.response.GxStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ResidentGxProgramDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentGxProgramListRes;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilityTypeRepository;
import com.apten.facilityreservation.domain.repository.GxProgramRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// GX 프로그램 관리 서비스이다.
@Service
@RequiredArgsConstructor
public class GxProgramService {

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
    private final FacilityTypeRepository facilityTypeRepository;
    private final GxProgramRepository gxProgramRepository;

    // GX 프로그램을 등록한다.
    @Transactional
    public GxProgramPostRes createGxProgram(Long complexId, GxProgramPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        // 시설 소속 및 GX 타입 검증
        Facility facility = validateGxFacility(complexId, req.getFacilityId());

        // 등록 입력값 검증
        validateGxProgramInput(req.getName(), req.getStartDate(), req.getEndDate(),
                req.getStartTime(), req.getEndTime(), req.getDaysOfWeek(),
                req.getMaxCount(), req.getMinCount(), req.getBaseFee());

        GxProgram program = gxProgramRepository.save(GxProgram.builder()
                .complexId(complexId)
                .facilityId(facility.getId())
                .name(req.getName())
                .description(req.getDescription())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .daysOfWeek(req.getDaysOfWeek())
                .maxCount(req.getMaxCount())
                .minCount(req.getMinCount() != null ? req.getMinCount() : 0)
                .baseFee(req.getBaseFee())
                .waitingEnabled(Boolean.TRUE.equals(req.getWaitingEnabled()))
                .build());

        return GxProgramPostRes.builder()
                .programId(program.getId())
                .name(program.getName())
                .status(program.getStatus())
                .createdAt(program.getCreatedAt())
                .build();
    }

    // 관리자 GX 프로그램 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<GxProgramListRes> getAdminGxProgramList(Long complexId, GxProgramListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize());

        // status enum 파라미터 null 비교 문제 회피: null 여부에 따라 메서드 분기
        Page<GxProgram> page = (req.getStatus() != null)
                ? gxProgramRepository.findAdminGxProgramsByStatus(
                        complexId, req.getStatus(), req.getFacilityId(),
                        req.getFromDate(), req.getToDate(), pageable)
                : gxProgramRepository.findAdminGxPrograms(
                        complexId, req.getFacilityId(),
                        req.getFromDate(), req.getToDate(), pageable);

        List<GxProgramListRes> items = page.getContent().stream()
                .map(p -> GxProgramListRes.builder()
                        .programId(p.getId())
                        .facilityId(p.getFacilityId())
                        .name(p.getName())
                        .startDate(p.getStartDate())
                        .endDate(p.getEndDate())
                        .startTime(p.getStartTime())
                        .endTime(p.getEndTime())
                        .baseFee(p.getBaseFee())
                        .waitingEnabled(p.getWaitingEnabled())
                        .status(p.getStatus())
                        // TODO: GxReservation 구현 후 실집계로 교체
                        .confirmedCount(0)
                        .waitingCount(0)
                        .build())
                .toList();

        return PageResponse.<GxProgramListRes>builder()
                .content(items)
                .page(req.getPage())
                .size(req.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 관리자 GX 프로그램 상세를 조회한다.
    @Transactional(readOnly = true)
    public GxProgramDetailRes getAdminGxProgramDetail(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = getGxProgram(complexId, programId);

        return GxProgramDetailRes.builder()
                .programId(program.getId())
                .complexId(program.getComplexId())
                .facilityId(program.getFacilityId())
                .name(program.getName())
                .description(program.getDescription())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .startTime(program.getStartTime())
                .endTime(program.getEndTime())
                .daysOfWeek(program.getDaysOfWeek())
                .maxCount(program.getMaxCount())
                .minCount(program.getMinCount())
                .baseFee(program.getBaseFee())
                .waitingEnabled(program.getWaitingEnabled())
                .status(program.getStatus())
                // TODO: GxReservation 구현 후 실집계로 교체
                .confirmedCount(0)
                .waitingCount(0)
                .rejectedCount(0)
                .build();
    }

    // GX 프로그램을 수정한다.
    @Transactional
    public GxProgramPatchRes updateGxProgram(Long complexId, Long programId, GxProgramPatchReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = getGxProgram(complexId, programId);

        // 취소된 프로그램은 수정 불가
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }

        // 수정 입력값 검증 (null 필드는 건너뜀)
        validateGxProgramPatchInput(req);

        program.apply(req);

        return GxProgramPatchRes.builder()
                .programId(program.getId())
                .name(program.getName())
                .status(program.getStatus())
                .updatedAt(program.getUpdatedAt())
                .build();
    }

    // GX 프로그램을 취소한다.
    @Transactional
    public GxProgramCancelRes cancelGxProgram(Long complexId, Long programId, GxProgramCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = getGxProgram(complexId, programId);

        // 이미 취소된 프로그램은 재취소 불가
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }

        program.cancel();

        // TODO: 관련 GxReservation 일괄 취소 처리 및 알림 발행은 2단계에서 구현

        return GxProgramCancelRes.builder()
                .programId(program.getId())
                .status(program.getStatus())
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 입주민 GX 프로그램 목록을 조회한다.
    public PageResponse<ResidentGxProgramListRes> getResidentGxProgramList(Long complexId, ResidentGxProgramListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO: 입주민 GX 프로그램 목록 조회 구현
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 입주민 GX 프로그램 상세를 조회한다.
    public ResidentGxProgramDetailRes getResidentGxProgramDetail(Long userId, Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO: 입주민 GX 프로그램 상세 조회 구현
        return ResidentGxProgramDetailRes.builder().programId(programId).build();
    }

    // GX 일괄 승인을 처리한다.
    public GxBulkApproveRes bulkApprove(Long complexId, Long programId, GxBulkApproveReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO: GxReservation 구현 후 WAITING → CONFIRMED 일괄 처리
        return GxBulkApproveRes.builder()
                .programId(programId)
                .approvedCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // GX 최소 인원 충족 여부를 검증한다.
    public GxMinimumCheckRes checkMinimum(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO: GxReservation 구현 후 CONFIRMED 인원 집계 및 minCount 비교
        return GxMinimumCheckRes.builder()
                .programId(programId)
                .minCount(0)
                .confirmedCount(0)
                .cancellable(false)
                .build();
    }

    // GX 현황을 조회한다.
    public GxStatusRes getGxStatus(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO: GxReservation 구현 후 상태별 집계
        return GxStatusRes.builder()
                .programId(programId)
                .confirmedCount(0)
                .waitingCount(0)
                .rejectedCount(0)
                .cancelledCount(0)
                .build();
    }

    // complexId 소속 GX 프로그램을 단건 조회한다.
    private GxProgram getGxProgram(Long complexId, Long programId) {
        return gxProgramRepository.findByIdAndComplexId(programId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));
    }

    // 시설 존재, complexId 소속, GX 타입, 활성 여부를 검증한다.
    private Facility validateGxFacility(Long complexId, Long facilityId) {
        if (facilityId == null) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        Facility facility = facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(facilityId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));

        if (Boolean.FALSE.equals(facility.getIsActive())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_INACTIVE);
        }

        // GX 타입 시설인지 확인
        FacilityType facilityType = facilityTypeRepository.findById(facility.getTypeId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_TYPE_NOT_FOUND));

        if (facilityType.getTypeCode() != FacilityTypeCode.GX) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_TYPE);
        }

        return facility;
    }

    // 등록 입력값의 유효성을 검증한다.
    private void validateGxProgramInput(
            String name,
            java.time.LocalDate startDate, java.time.LocalDate endDate,
            java.time.LocalTime startTime, java.time.LocalTime endTime,
            String daysOfWeek, Integer maxCount, Integer minCount, BigDecimal baseFee
    ) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (daysOfWeek == null || daysOfWeek.isBlank()) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (maxCount == null || maxCount <= 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        int resolvedMin = (minCount != null) ? minCount : 0;
        if (resolvedMin < 0 || resolvedMin > maxCount) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (baseFee != null && baseFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
    }

    // 수정 입력값의 유효성을 검증한다. null 필드는 검증을 건너뛴다.
    private void validateGxProgramPatchInput(GxProgramPatchReq req) {
        if (req.getName() != null && req.getName().isBlank()) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (req.getStartDate() != null && req.getEndDate() != null
                && req.getStartDate().isAfter(req.getEndDate())) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (req.getStartTime() != null && req.getEndTime() != null
                && !req.getStartTime().isBefore(req.getEndTime())) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (req.getDaysOfWeek() != null && req.getDaysOfWeek().isBlank()) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (req.getMaxCount() != null && req.getMaxCount() <= 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (req.getMinCount() != null && req.getMinCount() < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
        if (req.getBaseFee() != null && req.getBaseFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
    }
}
