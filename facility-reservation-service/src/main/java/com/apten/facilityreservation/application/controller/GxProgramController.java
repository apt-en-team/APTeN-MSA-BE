package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.GxProgramSearchReq;
import com.apten.facilityreservation.application.model.response.GxProgramRes;
import com.apten.facilityreservation.application.service.GxProgramService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

// GX 프로그램 조회 API 진입점
// 사용자가 GX 프로그램 목록을 조회할 때 이 컨트롤러를 사용한다
@RestController
@RequiredArgsConstructor
public class GxProgramController {

    private final GxProgramService gxProgramService;

    @GetMapping("/api/gx-programs")
    public ResultResponse<List<GxProgramRes>> getGxProgramList(@ModelAttribute GxProgramSearchReq request) {
        return ResultResponse.success("GX 프로그램 목록 조회 성공", gxProgramService.getGxProgramList(request));
    }
}
