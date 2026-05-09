package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
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
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// GX 프로그램 관리 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class GxProgramService {

    private final FeatureAccessService featureAccessService;

    // GX 프로그램을 등록한다.
    public GxProgramPostRes createGxProgram(Long complexId, GxProgramPostReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityId가 현재 complexId 소속인지 검증한다.
        // 3) GX 전용 또는 APPROVAL 정책과의 정합성을 확인한다.
        // 4) 날짜/시간/요일/정원 유효성을 검증한다.
        // 5) gx_program 저장 및 응답 DTO 변환을 수행한다.
        return GxProgramPostRes.builder()
                .programId(0L)
                .name(req.getName())
                .status(GxProgramStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 GX 프로그램 목록을 조회한다.
    public PageResponse<GxProgramListRes> getAdminGxProgramList(Long complexId, GxProgramListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) complexId 기준 GX 프로그램만 조회한다.
        // 3) status, fromDate, toDate, facilityId 필터를 적용한다.
        // 4) confirmedCount, waitingCount 집계를 포함해 응답 DTO를 구성한다.
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 관리자 GX 프로그램 상세를 조회한다.
    public GxProgramDetailRes getAdminGxProgramDetail(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) confirmed/waiting/rejected 집계를 함께 조회한다.
        return GxProgramDetailRes.builder().programId(programId).build();
    }

    // GX 프로그램을 수정한다.
    public GxProgramPatchRes updateGxProgram(Long complexId, Long programId, GxProgramPatchReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) 일정, 요일, 정원 수정값의 유효성을 검증한다.
        // 4) Entity 저장 및 응답 DTO 변환을 수행한다.
        return GxProgramPatchRes.builder()
                .programId(programId)
                .name(req.getName())
                .status(GxProgramStatus.OPEN)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // GX 프로그램을 취소한다.
    public GxProgramCancelRes cancelGxProgram(Long complexId, Long programId, GxProgramCancelReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) GX 프로그램 취소 가능 상태인지 확인한다.
        // 4) 관련 gx_reservation의 PROGRAM 취소 처리와 알림 발행은 2단계에서 구현한다.
        return GxProgramCancelRes.builder()
                .programId(programId)
                .status(GxProgramStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 입주민 GX 프로그램 목록을 조회한다.
    public PageResponse<ResidentGxProgramListRes> getResidentGxProgramList(Long complexId, ResidentGxProgramListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) 입주민 단지 컨텍스트 complexId 기준 프로그램만 조회한다.
        // 3) fromDate, toDate, status 필터를 적용한다.
        // 4) confirmedCount, waitingCount를 포함한 목록 응답을 구성한다.
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 입주민 GX 프로그램 상세를 조회한다.
    public ResidentGxProgramDetailRes getResidentGxProgramDetail(Long userId, Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) userId 기준 내 신청 상태와 대기 순번을 조회한다.
        // 4) confirmedCount, waitingCount를 포함해 응답 DTO를 구성한다.
        return ResidentGxProgramDetailRes.builder().programId(programId).build();
    }

    // GX 일괄 승인을 처리한다.
    public GxBulkApproveRes bulkApprove(Long complexId, Long programId, GxBulkApproveReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) WAITING 목록 조회와 승인 가능 인원 계산은 2단계에서 구현한다.
        // 4) 승인/대기 순번 재정렬과 알림 발행은 2단계에서 구현한다.
        return GxBulkApproveRes.builder()
                .programId(programId)
                .approvedCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // GX 최소 인원 충족 여부를 검증한다.
    public GxMinimumCheckRes checkMinimum(Long complexId, Long programId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) minCount와 현재 CONFIRMED 인원 비교 로직은 2단계에서 구현한다.
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
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) programId가 현재 complexId 소속인지 검증한다.
        // 3) confirmed/waiting/rejected/cancelled 집계는 2단계에서 구현한다.
        return GxStatusRes.builder()
                .programId(programId)
                .confirmedCount(0)
                .waitingCount(0)
                .rejectedCount(0)
                .cancelledCount(0)
                .build();
    }
}
