package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehicleCheckLicensePlateReq;
import com.apten.parkingvehicle.application.model.request.VehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VehiclePostReq;
import com.apten.parkingvehicle.application.model.response.VehicleCheckLicensePlateRes;
import com.apten.parkingvehicle.application.model.response.VehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VehiclePostRes;
import com.apten.parkingvehicle.application.model.response.VehicleRes;
import com.apten.parkingvehicle.application.service.VehicleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 입주민 차량 API 진입점
@RestController
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/api/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VehiclePostRes> createVehicle(@RequestBody VehiclePostReq request) {
        return ResultResponse.success("차량 등록 신청 성공", vehicleService.createVehicle(request));
    }

    @PatchMapping("/api/vehicles/{vehicleUid}")
    public ResultResponse<VehiclePatchRes> updateVehicle(
            @PathVariable String vehicleUid,
            @RequestBody VehiclePatchReq request
    ) {
        return ResultResponse.success("차량 수정 성공", vehicleService.updateVehicle(vehicleUid, request));
    }

    @DeleteMapping("/api/vehicles/{vehicleUid}")
    public ResultResponse<VehicleDeleteRes> deleteVehicle(@PathVariable String vehicleUid) {
        return ResultResponse.success("차량 삭제 성공", vehicleService.deleteVehicle(vehicleUid));
    }

    @GetMapping("/api/vehicles")
    public ResultResponse<List<VehicleRes>> getMyVehicleList() {
        return ResultResponse.success("내 차량 목록 조회 성공", vehicleService.getMyVehicleList());
    }

    @GetMapping("/api/vehicles/check-license-plate")
    public ResultResponse<VehicleCheckLicensePlateRes> checkDuplicateLicensePlate(
            @RequestParam String licensePlate
    ) {
        return ResultResponse.success(
                "차량번호 중복 확인 성공",
                vehicleService.checkDuplicateLicensePlate(VehicleCheckLicensePlateReq.builder().licensePlate(licensePlate).build())
        );
    }
}
