package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehiclePostReq;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleSearchReq;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleGetPageRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehiclePostRes;
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

    @PostMapping("/api/regular-visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<RegularVisitorVehiclePostRes> createRegularVisitorVehicle(
            @RequestBody RegularVisitorVehiclePostReq request
    ) {
        return ResultResponse.success("고정 방문차량 등록 성공", regularVisitorVehicleService.createRegularVisitorVehicle(request));
    }

    @GetMapping("/api/regular-visitor-vehicles")
    public ResultResponse<RegularVisitorVehicleGetPageRes> getMyRegularVisitorVehicleList(
            @ModelAttribute RegularVisitorVehicleSearchReq request
    ) {
        return ResultResponse.success(
                "고정 방문차량 목록 조회 성공",
                regularVisitorVehicleService.getMyRegularVisitorVehicleList(request)
        );
    }

    @DeleteMapping("/api/regular-visitor-vehicles/{regularVisitorVehicleUid}")
    public ResultResponse<RegularVisitorVehicleDeleteRes> deleteRegularVisitorVehicle(
            @PathVariable String regularVisitorVehicleUid
    ) {
        return ResultResponse.success(
                "고정 방문차량 삭제 성공",
                regularVisitorVehicleService.deleteRegularVisitorVehicle(regularVisitorVehicleUid)
        );
    }
}
