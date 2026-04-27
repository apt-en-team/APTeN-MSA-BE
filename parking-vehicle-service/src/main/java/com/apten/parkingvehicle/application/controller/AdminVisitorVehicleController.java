package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
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

    //관리자 방문차량 등록 API-317
    @PostMapping("/api/admin/visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminVisitorVehicleCreateRes> createAdminVisitorVehicle(@RequestBody AdminVisitorVehicleCreateReq request) {
        return ResultResponse.success("관리자 방문차량 등록 성공", visitorVehicleService.createAdminVisitorVehicle(request));
    }

    //방문 예정 차량 조회 API-318
    @GetMapping("/api/admin/visitor-vehicles")
    public ResultResponse<PageResponse<AdminVisitorVehicleListRes>> getAdminVisitorVehicleList(
            @ModelAttribute AdminVisitorVehicleListReq request
    ) {
        return ResultResponse.success("방문 예정 차량 목록 조회 성공", visitorVehicleService.getAdminVisitorVehicleList(request));
    }
}
