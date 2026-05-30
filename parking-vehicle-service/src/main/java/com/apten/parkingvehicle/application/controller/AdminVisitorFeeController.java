package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.response.AdminVisitorUsageMonthlyListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.service.ParkingFeeCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 방문차량 월 과금 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/visitor-vehicles/fees")
public class AdminVisitorFeeController {

    // 비용 산정/조회 응용 서비스
    private final ParkingFeeCalculationService parkingFeeCalculationService;

    // 단지의 특정 월 방문차량 과금 페이지 조회 (관리자)
    @GetMapping("/monthly")
    public ResultResponse<PageResponse<AdminVisitorUsageMonthlyListRes>> listMonthlyFees(
            @RequestParam("yearMonth") String yearMonth,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "방문차량 월 과금 목록 조회 성공",
                parkingFeeCalculationService.listAdminVisitorMonthlyFees(
                        yearMonth, page, size, userRole, complexId, selectedComplexId
                )
        );
    }
}
