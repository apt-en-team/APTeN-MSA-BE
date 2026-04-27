package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleListRes;
import com.apten.parkingvehicle.application.service.RegularVisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 고정 방문차량 사용자 API 진입점
@RestController
@RequiredArgsConstructor
public class RegularVisitorVehicleController {

    private final RegularVisitorVehicleService regularVisitorVehicleService;

    //고정 방문차량 등록 API-322
    @PostMapping("/api/regular-visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<RegularVisitorVehicleCreateRes> createRegularVisitorVehicle(
            @RequestBody RegularVisitorVehicleCreateReq request
    ) {
        return ResultResponse.success("고정 방문차량 등록 성공", regularVisitorVehicleService.createRegularVisitorVehicle(request));
    }

    //고정 방문차량 조회 API-323
    @GetMapping("/api/regular-visitor-vehicles")
    public ResultResponse<PageResponse<RegularVisitorVehicleListRes>> getMyRegularVisitorVehicleList(
            @ModelAttribute RegularVisitorVehicleListReq request
    ) {
        return ResultResponse.success(
                "고정 방문차량 목록 조회 성공",
                regularVisitorVehicleService.getMyRegularVisitorVehicleList(request)
        );
    }

    //고정 방문차량 삭제 API-324
    @DeleteMapping("/api/regular-visitor-vehicles/{regularVisitorVehicleId}")
    public ResultResponse<RegularVisitorVehicleDeleteRes> deleteRegularVisitorVehicle(
            @PathVariable Long regularVisitorVehicleId
    ) {
        return ResultResponse.success(
                "고정 방문차량 삭제 성공",
                regularVisitorVehicleService.deleteRegularVisitorVehicle(regularVisitorVehicleId)
        );
    }
}
