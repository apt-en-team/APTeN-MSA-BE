package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.AdminGxReservationListReq;
import com.apten.facilityreservation.application.model.request.GxBulkApproveReq;
import com.apten.facilityreservation.application.model.request.GxCloseWaitingReq;
import com.apten.facilityreservation.application.model.request.GxProgramCancelReq;
import com.apten.facilityreservation.application.model.request.GxProgramListReq;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.application.model.request.GxProgramPostReq;
import com.apten.facilityreservation.application.model.request.ResidentGxProgramListReq;
import com.apten.facilityreservation.application.model.response.AdminGxReservationListRes;
import com.apten.facilityreservation.application.model.response.GxBulkApproveRes;
import com.apten.facilityreservation.application.model.response.GxCloseWaitingRes;
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
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import com.apten.facilityreservation.domain.enums.GxReservationCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilityTypeRepository;
import com.apten.facilityreservation.domain.repository.GxProgramRepository;
import com.apten.facilityreservation.domain.repository.GxReservationRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

// GX 프로그램 관리
@Service
@RequiredArgsConstructor
public class GxProgramService {

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
    private final FacilityTypeRepository facilityTypeRepository;
    private final GxProgramRepository gxProgramRepository;
    private final GxReservationRepository gxReservationRepository;
    private final GxReservationService gxReservationService;
    private final UserCacheRepository userCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;
    private final FacilityNotificationService facilityNotificationService;

    // GX 프로그램 등록
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

    // 관리자 GX 프로그램 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<GxProgramListRes> getAdminGxProgramList(Long complexId, GxProgramListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize());

        // 상태 필터 분기
        Page<GxProgram> page = (req.getStatus() != null)
                ? gxProgramRepository.findAdminGxProgramsByStatus(
                        complexId, req.getStatus(), req.getFacilityId(),
                        req.getFromDate(), req.getToDate(), pageable)
                : gxProgramRepository.findAdminGxPrograms(
                        complexId, req.getFacilityId(),
                        req.getFromDate(), req.getToDate(), pageable);

        List<GxProgram> programs = page.getContent();
        List<Long> programIds = programs.stream().map(GxProgram::getId).toList();

        // 상태별 인원 일괄 집계 (N+1 방지)
        Map<Long, Long> confirmedMap = programIds.isEmpty() ? Map.of() :
                gxReservationRepository.countByProgramIdInAndStatus(programIds, GxReservationStatus.CONFIRMED)
                        .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        Map<Long, Long> waitingMap = programIds.isEmpty() ? Map.of() :
                gxReservationRepository.countByProgramIdInAndStatus(programIds, GxReservationStatus.WAITING)
                        .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        Map<Long, Long> cancelledMap = programIds.isEmpty() ? Map.of() :
                gxReservationRepository.countByProgramIdInAndStatus(programIds, GxReservationStatus.CANCELLED)
                        .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        Map<Long, Long> rejectedMap = programIds.isEmpty() ? Map.of() :
                gxReservationRepository.countByProgramIdInAndStatus(programIds, GxReservationStatus.REJECTED)
                        .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<GxProgramListRes> items = programs.stream()
                .map(p -> GxProgramListRes.builder()
                        .programId(p.getId())
                        .facilityId(p.getFacilityId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .startDate(p.getStartDate())
                        .endDate(p.getEndDate())
                        .startTime(p.getStartTime())
                        .endTime(p.getEndTime())
                        .daysOfWeek(p.getDaysOfWeek())
                        .minCount(p.getMinCount())
                        .maxCount(p.getMaxCount())
                        .baseFee(p.getBaseFee())
                        .waitingEnabled(p.getWaitingEnabled())
                        .status(p.getStatus())
                        .confirmedCount(confirmedMap.getOrDefault(p.getId(), 0L).intValue())
                        .waitingCount(waitingMap.getOrDefault(p.getId(), 0L).intValue())
                        .cancelledCount(
                                cancelledMap.getOrDefault(p.getId(), 0L).intValue() +
                                rejectedMap.getOrDefault(p.getId(), 0L).intValue())
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

    // 관리자 GX 프로그램 상세 조회
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
                .confirmedCount((int) gxReservationRepository.countByProgramIdAndStatus(program.getId(), GxReservationStatus.CONFIRMED))
                .waitingCount((int) gxReservationRepository.countByProgramIdAndStatus(program.getId(), GxReservationStatus.WAITING))
                .rejectedCount((int) gxReservationRepository.countByProgramIdAndStatus(program.getId(), GxReservationStatus.REJECTED))
                .build();
    }

    // GX 프로그램 수정
    @Transactional
    public GxProgramPatchRes updateGxProgram(Long complexId, Long programId, GxProgramPatchReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = getGxProgram(complexId, programId);

        // 취소 프로그램 수정 방지
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }

        // 수정 입력값 검증 (null 제외)
        validateGxProgramPatchInput(req);

        program.apply(req);

        return GxProgramPatchRes.builder()
                .programId(program.getId())
                .name(program.getName())
                .status(program.getStatus())
                .updatedAt(program.getUpdatedAt())
                .build();
    }

    // GX 프로그램 취소
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GxProgramCancelRes cancelGxProgram(Long complexId, Long programId, GxProgramCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = lockGxProgram(complexId, programId);

        // 프로그램 재취소 방지
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }

        program.cancel();

        // 활성 신청 일괄 취소
        List<GxReservation> waitingReservations = gxReservationRepository.findByProgramIdAndStatus(programId, GxReservationStatus.WAITING);
        List<GxReservation> confirmedReservations = gxReservationRepository.findByProgramIdAndStatus(programId, GxReservationStatus.CONFIRMED);

        waitingReservations.forEach(r -> {
            r.cancel(GxReservationCancelReason.ADMIN);
            // 프로그램 취소 결과 알림 (GX 거절)
            facilityNotificationService.notifyGxRejected(r.getUserId(), complexId, r, program);
        });
        confirmedReservations.forEach(r -> {
            r.cancel(GxReservationCancelReason.ADMIN);
            // 승인 신청자 취소 결과 알림
            facilityNotificationService.notifyGxRejected(r.getUserId(), complexId, r, program);
        });

        return GxProgramCancelRes.builder()
                .programId(program.getId())
                .status(program.getStatus())
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 입주민 GX 프로그램 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<ResidentGxProgramListRes> getResidentGxProgramList(Long complexId, ResidentGxProgramListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        int page = req.getPage() != null ? Math.max(req.getPage(), 0) : 0;
        int size = req.getSize() != null && req.getSize() > 0 ? req.getSize() : 20;

        // 취소 프로그램 노출 제외
        if (req.getStatus() == GxProgramStatus.CANCELLED) {
            return PageResponse.empty(page, size);
        }

        PageRequest pageable = PageRequest.of(page, size);

        // 상태 필터 분기
        Page<GxProgram> programPage = (req.getStatus() != null)
                ? gxProgramRepository.findResidentGxProgramsByStatus(complexId, req.getStatus(), req.getFromDate(), req.getToDate(), pageable)
                : gxProgramRepository.findResidentGxPrograms(complexId, GxProgramStatus.CANCELLED, req.getFromDate(), req.getToDate(), pageable);

        List<GxProgram> programs = programPage.getContent();

        if (programs.isEmpty()) {
            return PageResponse.<ResidentGxProgramListRes>builder()
                    .content(List.of())
                    .page(page)
                    .size(size)
                    .totalElements(programPage.getTotalElements())
                    .totalPages(programPage.getTotalPages())
                    .hasNext(false)
                    .build();
        }

        List<Long> programIds = programs.stream().map(GxProgram::getId).toList();

        // 확정/대기 인원 일괄 집계 (N+1 방지)
        Map<Long, Long> confirmedMap = gxReservationRepository
                .countByProgramIdInAndStatus(programIds, GxReservationStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        Map<Long, Long> waitingMap = gxReservationRepository
                .countByProgramIdInAndStatus(programIds, GxReservationStatus.WAITING)
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<ResidentGxProgramListRes> items = programs.stream()
                .map(p -> ResidentGxProgramListRes.builder()
                        .programId(p.getId())
                        .name(p.getName())
                        .facilityId(p.getFacilityId())
                        .startDate(p.getStartDate())
                        .endDate(p.getEndDate())
                        .startTime(p.getStartTime())
                        .endTime(p.getEndTime())
                        .baseFee(p.getBaseFee())
                        .maxCount(p.getMaxCount())
                        .confirmedCount(confirmedMap.getOrDefault(p.getId(), 0L).intValue())
                        .waitingCount(waitingMap.getOrDefault(p.getId(), 0L).intValue())
                        .status(p.getStatus())
                        .build())
                .toList();

        return PageResponse.<ResidentGxProgramListRes>builder()
                .content(items)
                .page(page)
                .size(size)
                .totalElements(programPage.getTotalElements())
                .totalPages(programPage.getTotalPages())
                .hasNext(programPage.hasNext())
                .build();
    }

    // 입주민 GX 프로그램 상세 조회
    @Transactional(readOnly = true)
    public ResidentGxProgramDetailRes getResidentGxProgramDetail(Long userId, Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = gxProgramRepository.findByIdAndComplexId(programId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));

        // 취소 프로그램 노출 제외
        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }

        // 확정/대기 인원 집계
        int confirmedCount = (int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.CONFIRMED);
        int waitingCount = (int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.WAITING);

        // 내 신청 상태/대기 순번 조회
        GxReservation myReservation = gxReservationRepository.findByProgramIdAndUserId(programId, userId).orElse(null);

        return ResidentGxProgramDetailRes.builder()
                .programId(program.getId())
                .name(program.getName())
                .facilityId(program.getFacilityId())
                .description(program.getDescription())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .startTime(program.getStartTime())
                .endTime(program.getEndTime())
                .daysOfWeek(program.getDaysOfWeek())
                .baseFee(program.getBaseFee())
                .maxCount(program.getMaxCount())
                .minCount(program.getMinCount())
                .confirmedCount(confirmedCount)
                .waitingCount(waitingCount)
                .myStatus(myReservation != null ? myReservation.getStatus() : null)
                .myWaitNo(myReservation != null ? myReservation.getWaitNo() : null)
                .status(program.getStatus())
                .build();
    }

    // GX 신청 일괄 승인
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GxBulkApproveRes bulkApprove(Long complexId, Long programId, GxBulkApproveReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = lockGxProgram(complexId, programId);

        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }

        long confirmedCount = gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.CONFIRMED);
        long availableSlots = program.getMaxCount() - confirmedCount;

        if (availableSlots <= 0) {
            return GxBulkApproveRes.builder()
                    .programId(programId)
                    .approvedCount(0)
                    .processedAt(LocalDateTime.now())
                    .build();
        }

        // 승인 인원 계산 (null이면 가용 인원 전체)
        long toApprove = (req.getApproveCount() != null)
                ? Math.min(req.getApproveCount(), availableSlots)
                : availableSlots;

        List<GxReservation> waitingList = gxReservationRepository
                .findByProgramIdAndStatusOrderByWaitNoAsc(programId, GxReservationStatus.WAITING);

        int approved = 0;
        for (GxReservation waiting : waitingList) {
            if (approved >= toApprove) break;
            waiting.approve();
            // 승인 신청자별 결과 알림 예약
            facilityNotificationService.notifyGxApproved(waiting.getUserId(), complexId, waiting, program);
            approved++;
        }

        // 대기 순번 재정렬
        if (approved > 0) {
            gxReservationService.resequenceWaitingNos(programId);
        }

        return GxBulkApproveRes.builder()
                .programId(programId)
                .approvedCount(approved)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // GX 최소 인원 충족 여부 조회
    @Transactional(readOnly = true)
    public GxMinimumCheckRes checkMinimum(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = getGxProgram(complexId, programId);

        int confirmedCount = (int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.CONFIRMED);

        return GxMinimumCheckRes.builder()
                .programId(programId)
                .minCount(program.getMinCount())
                .confirmedCount(confirmedCount)
                .cancellable(confirmedCount < program.getMinCount())
                .build();
    }

    // GX 현황 조회
    @Transactional(readOnly = true)
    public GxStatusRes getGxStatus(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        getGxProgram(complexId, programId);

        return GxStatusRes.builder()
                .programId(programId)
                .confirmedCount((int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.CONFIRMED))
                .waitingCount((int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.WAITING))
                .rejectedCount((int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.REJECTED))
                .cancelledCount((int) gxReservationRepository.countByProgramIdAndStatus(programId, GxReservationStatus.CANCELLED))
                .build();
    }

    // GX 프로그램 모집 마감
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GxCloseWaitingRes closeWaiting(Long complexId, Long programId, GxCloseWaitingReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        GxProgram program = lockGxProgram(complexId, programId);

        if (program.getStatus() == GxProgramStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_CANCELLED);
        }
        if (program.getStatus() == GxProgramStatus.CLOSED || program.getStatus() == GxProgramStatus.WAITING_CLOSED) {
            throw new BusinessException(FacilityReservationErrorCode.GX_RECRUITING_CLOSED);
        }

        String rejectReason = (req != null && req.getRejectReason() != null && !req.getRejectReason().isBlank())
                ? req.getRejectReason()
                : "모집 마감으로 인한 거절";

        // 잔여 대기 신청 일괄 거절
        List<GxReservation> waitingList = gxReservationRepository
                .findByProgramIdAndStatus(programId, GxReservationStatus.WAITING);
        waitingList.forEach(r -> {
            r.reject(rejectReason);
            // 모집 마감 거절 알림 예약 (afterCommit)
            facilityNotificationService.notifyGxRejected(r.getUserId(), complexId, r, program);
        });

        program.closeWaiting();

        return GxCloseWaitingRes.builder()
                .programId(program.getId())
                .status(program.getStatus())
                .rejectedCount(waitingList.size())
                .processedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 GX 신청자 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<AdminGxReservationListRes> getAdminGxReservationList(Long complexId, Long programId, AdminGxReservationListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        getGxProgram(complexId, programId);

        List<GxReservation> reservations = req.getStatus() != null
                ? gxReservationRepository.findByProgramIdAndStatusOrderByCreatedAtAsc(programId, req.getStatus())
                : gxReservationRepository.findByProgramIdOrderByCreatedAtAsc(programId);

        if (reservations.isEmpty()) {
            return PageResponse.empty(req.getPage(), req.getSize());
        }

        List<Long> userIds = reservations.stream()
                .map(GxReservation::getUserId).filter(id -> id != null).distinct().toList();
        List<Long> householdIds = reservations.stream()
                .map(GxReservation::getHouseholdId).filter(id -> id != null).distinct().toList();

        Map<Long, UserCache> userMap = userIds.isEmpty() ? Map.of()
                : userCacheRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(UserCache::getId, Function.identity()));
        Map<Long, HouseholdCache> householdMap = householdIds.isEmpty() ? Map.of()
                : householdCacheRepository.findAllById(householdIds).stream()
                        .collect(Collectors.toMap(HouseholdCache::getHouseholdId, Function.identity()));

        List<AdminGxReservationListRes> items = reservations.stream().map(r -> {
            UserCache user = userMap.get(r.getUserId());
            HouseholdCache household = householdMap.get(r.getHouseholdId());
            return AdminGxReservationListRes.builder()
                    .gxReservationId(r.getId())
                    .userId(r.getUserId())
                    .householdId(r.getHouseholdId())
                    .residentName(user != null ? user.getName() : null)
                    .dong(household != null ? household.getBuildingNo() : null)
                    .ho(household != null ? household.getUnitNo() : null)
                    .unit(buildUnit(household))
                    .status(r.getStatus())
                    .statusName(r.getStatus() != null ? r.getStatus().getValue() : null)
                    .waitNo(r.getWaitNo())
                    .approvedAt(r.getApprovedAt())
                    .rejectReason(r.getRejectReason())
                    .cancelReason(r.getCancelReason())
                    .cancelledAt(r.getCancelledAt())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).toList();

        int page = Math.max(req.getPage(), 0);
        int size = req.getSize() > 0 ? req.getSize() : 20;
        return buildPageResponse(items, page, size);
    }

    // GX 승인 리마인더 발송 (OPEN + WAITING)
    @Transactional(readOnly = true)
    public void sendApprovalReminderNotifications(LocalDate targetStartDate) {
        List<GxProgram> programs = gxProgramRepository.findByStartDateAndStatusIn(
                targetStartDate, List.of(GxProgramStatus.OPEN));

        for (GxProgram program : programs) {
            long waitingCount = gxReservationRepository.countByProgramIdAndStatus(
                    program.getId(), GxReservationStatus.WAITING);
            if (waitingCount > 0) {
                facilityNotificationService.notifyGxApprovalReminder(program.getComplexId(), program);
            }
        }
    }

    // 단지 GX 프로그램 조회
    private GxProgram getGxProgram(Long complexId, Long programId) {
        return gxProgramRepository.findByIdAndComplexId(programId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));
    }

    private GxProgram lockGxProgram(Long complexId, Long programId) {
        return gxProgramRepository.findByIdAndComplexIdForUpdate(programId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND));
    }

    // 목록 페이지 응답 생성
    private <T> PageResponse<T> buildPageResponse(List<T> items, int page, int size) {
        long total = items.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int fromIndex = page * size;
        int toIndex = (int) Math.min((long) fromIndex + size, total);
        return PageResponse.<T>builder()
                .content(fromIndex >= total ? List.of() : items.subList(fromIndex, toIndex))
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(toIndex < total)
                .build();
    }

    // 세대 동/호 표시명 생성
    private String buildUnit(HouseholdCache household) {
        if (household == null
                || household.getBuildingNo() == null
                || household.getUnitNo() == null) {
            return null;
        }
        return household.getBuildingNo() + "동 " + household.getUnitNo() + "호";
    }

    // GX 시설 검증
    private Facility validateGxFacility(Long complexId, Long facilityId) {
        if (facilityId == null) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        Facility facility = facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(facilityId, complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));

        if (Boolean.FALSE.equals(facility.getIsActive())) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_INACTIVE);
        }

        // GX 시설 유형 확인
        FacilityType facilityType = facilityTypeRepository.findById(facility.getTypeId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_TYPE_NOT_FOUND));

        if (facilityType.getTypeCode() != FacilityTypeCode.GX) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_TYPE);
        }

        return facility;
    }

    // GX 프로그램 등록값 검증
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

    // GX 프로그램 수정값 검증 (null 제외)
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
