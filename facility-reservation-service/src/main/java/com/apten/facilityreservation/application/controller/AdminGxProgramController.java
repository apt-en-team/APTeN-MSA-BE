package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.GxBulkApproveReq;
import com.apten.facilityreservation.application.model.request.GxProgramCancelReq;
import com.apten.facilityreservation.application.model.request.GxProgramListReq;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.application.model.request.GxProgramPostReq;
import com.apten.facilityreservation.application.model.response.GxBulkApproveRes;
import com.apten.facilityreservation.application.model.response.GxMinimumCheckRes;
import com.apten.facilityreservation.application.model.response.GxProgramCancelRes;
import com.apten.facilityreservation.application.model.response.GxProgramDetailRes;
import com.apten.facilityreservation.application.model.response.GxProgramListRes;
import com.apten.facilityreservation.application.model.response.GxProgramPatchRes;
import com.apten.facilityreservation.application.model.response.GxProgramPostRes;
import com.apten.facilityreservation.application.model.response.GxStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.service.GxProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 GX 프로그램 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminGxProgramController {

    private final GxProgramService gxProgramService;

    //GX 프로그램 등록 API-630
    @PostMapping("/api/admin/gx-programs")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<GxProgramPostRes> createGxProgram(@RequestBody GxProgramPostReq req) {
        return ResultResponse.success("GX 프로그램 등록 성공", gxProgramService.createGxProgram(req));
    }

    //GX 프로그램 목록 조회 API-631
    @GetMapping("/api/admin/gx-programs")
    public ResultResponse<PageResponse<GxProgramListRes>> getAdminGxProgramList(@ModelAttribute GxProgramListReq req) {
        return ResultResponse.success("GX 프로그램 목록 조회 성공", gxProgramService.getAdminGxProgramList(req));
    }

    //GX 프로그램 상세 조회 API-632
    @GetMapping("/api/admin/gx-programs/{programId}")
    public ResultResponse<GxProgramDetailRes> getAdminGxProgramDetail(@PathVariable Long programId) {
        return ResultResponse.success("GX 프로그램 상세 조회 성공", gxProgramService.getAdminGxProgramDetail(programId));
    }

    //GX 프로그램 수정 API-633
    @PatchMapping("/api/admin/gx-programs/{programId}")
    public ResultResponse<GxProgramPatchRes> updateGxProgram(@PathVariable Long programId, @RequestBody GxProgramPatchReq req) {
        return ResultResponse.success("GX 프로그램 수정 성공", gxProgramService.updateGxProgram(programId, req));
    }

    //GX 프로그램 취소 API-634
    @PatchMapping("/api/admin/gx-programs/{programId}/cancel")
    public ResultResponse<GxProgramCancelRes> cancelGxProgram(@PathVariable Long programId, @RequestBody GxProgramCancelReq req) {
        return ResultResponse.success("GX 프로그램 취소 성공", gxProgramService.cancelGxProgram(programId, req));
    }

    //GX 일괄 승인 처리 API-642
    @PostMapping("/api/admin/gx-programs/{programId}/bulk-approve")
    public ResultResponse<GxBulkApproveRes> bulkApprove(@PathVariable Long programId, @RequestBody GxBulkApproveReq req) {
        return ResultResponse.success("GX 일괄 승인 처리 성공", gxProgramService.bulkApprove(programId, req));
    }

    //GX 최소 인원 검증 API-643
    @PostMapping("/api/admin/gx-programs/{programId}/minimum-check")
    public ResultResponse<GxMinimumCheckRes> checkMinimum(@PathVariable Long programId) {
        return ResultResponse.success("GX 최소 인원 검증 성공", gxProgramService.checkMinimum(programId));
    }

    //GX 현황 조회 API-647
    @GetMapping("/api/admin/gx-programs/{programId}/status")
    public ResultResponse<GxStatusRes> getGxStatus(@PathVariable Long programId) {
        return ResultResponse.success("GX 현황 조회 성공", gxProgramService.getGxStatus(programId));
    }
}
