package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VisitorUsageMonthlyAggregateReq;
import com.apten.parkingvehicle.application.model.response.VisitorUsageMonthlyAggregateRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.service.VisitorUsageService;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 내부 집계와 방문차량 만료 처리 API 진입점
@RestController
@RequiredArgsConstructor
public class InternalVisitorUsageController {

    private final VisitorUsageService visitorUsageService;
    private final VisitorVehicleService visitorVehicleService;

    @PostMapping("/internal/visitor-usage-monthly/aggregate")
    public ResultResponse<VisitorUsageMonthlyAggregateRes> aggregateVisitorUsageMonthly(
            @RequestBody VisitorUsageMonthlyAggregateReq request
    ) {
        return ResultResponse.success("방문차량 월별 이용시간 집계 실행 성공", visitorUsageService.aggregateVisitorUsageMonthly(request));
    }

    @PostMapping("/internal/visitor-vehicles/expire")
    public ResultResponse<VisitorVehicleExpireRes> expireVisitorVehicles() {
        return ResultResponse.success("방문차량 자동 만료 실행 성공", visitorVehicleService.expireVisitorVehicles());
    }
}
