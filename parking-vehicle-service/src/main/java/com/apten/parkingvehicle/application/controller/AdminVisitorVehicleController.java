package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehiclePostReq;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleSearchReq;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleGetPageRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehiclePostRes;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 방문차량 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminVisitorVehicleController {

    private final VisitorVehicleService visitorVehicleService;

    @PostMapping("/api/admin/visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminVisitorVehiclePostRes> createAdminVisitorVehicle(@RequestBody AdminVisitorVehiclePostReq request) {
        return ResultResponse.success("관리자 방문차량 등록 성공", visitorVehicleService.createAdminVisitorVehicle(request));
    }

    @GetMapping("/api/admin/visitor-vehicles")
    public ResultResponse<AdminVisitorVehicleGetPageRes> getAdminVisitorVehicleList(
            @ModelAttribute AdminVisitorVehicleSearchReq request
    ) {
        return ResultResponse.success("방문 예정 차량 목록 조회 성공", visitorVehicleService.getAdminVisitorVehicleList(request));
    }
}
