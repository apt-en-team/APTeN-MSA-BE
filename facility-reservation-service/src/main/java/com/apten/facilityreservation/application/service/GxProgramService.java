package com.apten.facilityreservation.application.service;

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
import org.springframework.stereotype.Service;

// GX 프로그램 관리 API 시그니처를 담당하는 서비스이다.
@Service
public class GxProgramService {

    // GX 프로그램을 등록한다.
    public GxProgramPostRes createGxProgram(GxProgramPostReq req) {
        //TODO complex_cache에서 단지 활성 상태 확인
        //TODO facility 활성 여부와 APPROVAL 타입 여부 확인
        //TODO GX 프로그램 저장
        return GxProgramPostRes.builder()
                .programId(0L)
                .name(req.getName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 GX 프로그램 목록을 조회한다.
    public PageResponse<GxProgramListRes> getAdminGxProgramList(GxProgramListReq req) {
        //TODO complexId 기준 GX 프로그램 목록 조회
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 관리자 GX 프로그램 상세를 조회한다.
    public GxProgramDetailRes getAdminGxProgramDetail(Long programId) {
        //TODO programId 기준 GX 프로그램 상세 조회
        return GxProgramDetailRes.builder().programId(programId).build();
    }

    // GX 프로그램을 수정한다.
    public GxProgramPatchRes updateGxProgram(Long programId, GxProgramPatchReq req) {
        //TODO GX 프로그램 존재 여부 확인
        //TODO 일정과 정원 값 수정
        return GxProgramPatchRes.builder()
                .programId(programId)
                .name(req.getName())
                .status(GxProgramStatus.OPEN)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // GX 프로그램을 취소한다.
    public GxProgramCancelRes cancelGxProgram(Long programId, GxProgramCancelReq req) {
        //TODO gx_program 상태 CANCELLED로 변경
        //TODO 관련 gx_reservation을 CANCELLED로 변경
        //TODO cancelReason PROGRAM 저장
        //TODO 프로그램 취소 알림 이벤트 outbox 적재
        return GxProgramCancelRes.builder()
                .programId(programId)
                .status(GxProgramStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    // 입주민 GX 프로그램 목록을 조회한다.
    public PageResponse<ResidentGxProgramListRes> getResidentGxProgramList(ResidentGxProgramListReq req) {
        //TODO 입주민 소속 단지 기준 프로그램 목록 조회
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 입주민 GX 프로그램 상세를 조회한다.
    public ResidentGxProgramDetailRes getResidentGxProgramDetail(Long programId) {
        //TODO programId 기준 입주민 GX 상세 조회
        return ResidentGxProgramDetailRes.builder().programId(programId).build();
    }

    // GX 일괄 승인을 처리한다.
    public GxBulkApproveRes bulkApprove(Long programId, GxBulkApproveReq req) {
        //TODO programId 기준 WAITING 목록을 waitNo 순으로 조회
        //TODO 현재 CONFIRMED 인원과 maxCount 차이 계산
        //TODO approveCount와 잔여 가능 인원 중 작은 값만 승인
        //TODO 승인된 예약 상태를 CONFIRMED로 변경
        //TODO 남은 WAITING은 waitNo 유지
        return GxBulkApproveRes.builder()
                .programId(programId)
                .approvedCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // GX 최소 인원 충족 여부를 검증한다.
    public GxMinimumCheckRes checkMinimum(Long programId) {
        //TODO minCount와 현재 CONFIRMED 인원 비교
        return GxMinimumCheckRes.builder()
                .programId(programId)
                .minCount(0)
                .currentCount(0)
                .satisfied(false)
                .build();
    }

    // GX 현황을 조회한다.
    public GxStatusRes getGxStatus(Long programId) {
        //TODO confirmed, waiting, rejected, cancelled 집계 조회
        return GxStatusRes.builder()
                .programId(programId)
                .confirmedCount(0)
                .waitingCount(0)
                .rejectedCount(0)
                .cancelledCount(0)
                .build();
    }
}
