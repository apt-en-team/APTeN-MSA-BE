package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.service.RegularVisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 관리자 고정 방문차량 삭제 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminRegularVisitorVehicleController {

    private final RegularVisitorVehicleService regularVisitorVehicleService;

    @DeleteMapping("/api/admin/regular-visitor-vehicles/{regularVisitorVehicleUid}")
    public ResultResponse<AdminRegularVisitorVehicleDeleteRes> deleteRegularVisitorVehicleByAdmin(
            @PathVariable String regularVisitorVehicleUid
    ) {
        return ResultResponse.success(
                "고정 방문차량 강제 삭제 성공",
                regularVisitorVehicleService.deleteRegularVisitorVehicleByAdmin(regularVisitorVehicleUid)
        );
    }
}
