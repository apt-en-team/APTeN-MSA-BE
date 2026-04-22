package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePostReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleReRegisterReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleSearchReq;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCancelRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleGetPageRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePostRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleReRegisterRes;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 방문차량 사용자 API 진입점
@RestController
@RequiredArgsConstructor
public class VisitorVehicleController {

    private final VisitorVehicleService visitorVehicleService;

    @PostMapping("/api/visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VisitorVehiclePostRes> createVisitorVehicle(@RequestBody VisitorVehiclePostReq request) {
        return ResultResponse.success("방문차량 등록 성공", visitorVehicleService.createVisitorVehicle(request));
    }

    @GetMapping("/api/visitor-vehicles")
    public ResultResponse<VisitorVehicleGetPageRes> getMyVisitorVehicleList(@ModelAttribute VisitorVehicleSearchReq request) {
        return ResultResponse.success("방문차량 목록 조회 성공", visitorVehicleService.getMyVisitorVehicleList(request));
    }

    @PatchMapping("/api/visitor-vehicles/{visitorVehicleUid}")
    public ResultResponse<VisitorVehiclePatchRes> updateVisitorVehicle(
            @PathVariable String visitorVehicleUid,
            @RequestBody VisitorVehiclePatchReq request
    ) {
        return ResultResponse.success("방문차량 수정 성공", visitorVehicleService.updateVisitorVehicle(visitorVehicleUid, request));
    }

    @PatchMapping("/api/visitor-vehicles/{visitorVehicleUid}/cancel")
    public ResultResponse<VisitorVehicleCancelRes> cancelVisitorVehicle(@PathVariable String visitorVehicleUid) {
        return ResultResponse.success("방문차량 취소 성공", visitorVehicleService.cancelVisitorVehicle(visitorVehicleUid));
    }

    @DeleteMapping("/api/visitor-vehicles/{visitorVehicleUid}")
    public ResultResponse<VisitorVehicleDeleteRes> deleteVisitorVehicle(@PathVariable String visitorVehicleUid) {
        return ResultResponse.success("방문차량 삭제 성공", visitorVehicleService.deleteVisitorVehicle(visitorVehicleUid));
    }

    @PostMapping("/api/visitor-vehicles/{visitorVehicleUid}/re-register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VisitorVehicleReRegisterRes> reRegisterVisitorVehicle(
            @PathVariable String visitorVehicleUid,
            @RequestBody VisitorVehicleReRegisterReq request
    ) {
        return ResultResponse.success("방문차량 재등록 성공", visitorVehicleService.reRegisterVisitorVehicle(visitorVehicleUid, request));
    }
}
