package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.ParkingFloorSearchReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogPostReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogSearchReq;
import com.apten.parkingvehicle.application.model.request.ParkingStatisticsSearchReq;
import com.apten.parkingvehicle.application.model.response.ParkingFloorRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogGetPageRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogPostRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatisticsRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatusRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePolicySummaryRes;
import com.apten.parkingvehicle.application.service.ParkingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 주차 조회와 입출차 등록 API 진입점
@RestController
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @GetMapping("/api/admin/parking-logs")
    public ResultResponse<ParkingLogGetPageRes> getParkingLogList(@ModelAttribute ParkingLogSearchReq request) {
        return ResultResponse.success("입출차 기록 조회 성공", parkingService.getParkingLogList(request));
    }

    @PostMapping("/api/admin/parking-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingLogPostRes> createParkingLog(@RequestBody ParkingLogPostReq request) {
        return ResultResponse.success("입출차 등록 성공", parkingService.createParkingLog(request));
    }

    @GetMapping("/api/admin/parking/status")
    public ResultResponse<ParkingStatusRes> getParkingStatus() {
        return ResultResponse.success("주차 현황 조회 성공", parkingService.getParkingStatus());
    }

    @GetMapping("/api/admin/parking/floors")
    public ResultResponse<List<ParkingFloorRes>> getParkingFloorList(@ModelAttribute ParkingFloorSearchReq request) {
        return ResultResponse.success("층별 주차 현황 조회 성공", parkingService.getParkingFloorList(request));
    }

    @GetMapping("/api/admin/parking/statistics")
    public ResultResponse<ParkingStatisticsRes> getParkingStatistics(@ModelAttribute ParkingStatisticsSearchReq request) {
        return ResultResponse.success("주차 통계 조회 성공", parkingService.getParkingStatistics(request));
    }

    @GetMapping("/api/admin/visitor-vehicle-policies/summary")
    public ResultResponse<VisitorVehiclePolicySummaryRes> getVisitorVehiclePolicySummary() {
        return ResultResponse.success("방문차량 비용 반영 기준 조회 성공", parkingService.getVisitorVehiclePolicySummary());
    }
}
