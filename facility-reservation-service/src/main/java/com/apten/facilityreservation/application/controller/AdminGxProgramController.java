package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.GxProgramCancelReq;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.application.model.request.GxProgramPostReq;
import com.apten.facilityreservation.application.model.response.GxProgramCancelRes;
import com.apten.facilityreservation.application.model.response.GxProgramPatchRes;
import com.apten.facilityreservation.application.model.response.GxProgramPostRes;
import com.apten.facilityreservation.application.service.GxProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 GX 프로그램 관리 API 진입점
// 프로그램 등록과 수정, 취소 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class AdminGxProgramController {

    private final GxProgramService gxProgramService;

    @PostMapping("/api/admin/gx-programs")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<GxProgramPostRes> createGxProgram(@RequestBody GxProgramPostReq request) {
        return ResultResponse.success("GX 프로그램 등록 성공", gxProgramService.createGxProgram(request));
    }

    @PatchMapping("/api/admin/gx-programs/{gxProgramUid}")
    public ResultResponse<GxProgramPatchRes> updateGxProgram(
            @PathVariable String gxProgramUid,
            @RequestBody GxProgramPatchReq request
    ) {
        return ResultResponse.success("GX 프로그램 수정 성공", gxProgramService.updateGxProgram(gxProgramUid, request));
    }

    @PatchMapping("/api/admin/gx-programs/{gxProgramUid}/cancel")
    public ResultResponse<GxProgramCancelRes> cancelGxProgram(
            @PathVariable String gxProgramUid,
            @RequestBody GxProgramCancelReq request
    ) {
        return ResultResponse.success("GX 프로그램 취소 성공", gxProgramService.cancelGxProgram(gxProgramUid, request));
    }
}
