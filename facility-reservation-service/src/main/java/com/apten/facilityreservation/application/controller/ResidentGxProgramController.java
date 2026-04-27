package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.ResidentGxProgramListReq;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ResidentGxProgramDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentGxProgramListRes;
import com.apten.facilityreservation.application.service.GxProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 입주민 GX 프로그램 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ResidentGxProgramController {

    private final GxProgramService gxProgramService;

    //입주민 GX 프로그램 목록 조회 API-635
    @GetMapping("/api/gx-programs")
    public ResultResponse<PageResponse<ResidentGxProgramListRes>> getResidentGxProgramList(@ModelAttribute ResidentGxProgramListReq req) {
        return ResultResponse.success("입주민 GX 프로그램 목록 조회 성공", gxProgramService.getResidentGxProgramList(req));
    }

    //입주민 GX 프로그램 상세 조회 API-636
    @GetMapping("/api/gx-programs/{programId}")
    public ResultResponse<ResidentGxProgramDetailRes> getResidentGxProgramDetail(@PathVariable Long programId) {
        return ResultResponse.success("입주민 GX 프로그램 상세 조회 성공", gxProgramService.getResidentGxProgramDetail(programId));
    }
}
