package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.ParkingFloorListReq;
import com.apten.parkingvehicle.application.model.request.ParkingFloorPatchReq;
import com.apten.parkingvehicle.application.model.request.ParkingFloorPostReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogCreateReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogListReq;
import com.apten.parkingvehicle.application.model.request.ParkingStatisticsReq;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.ParkingFloorListRes;
import com.apten.parkingvehicle.application.model.response.ParkingFloorPatchRes;
import com.apten.parkingvehicle.application.model.response.ParkingFloorPostRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogCreateRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogListRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatisticsRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatusRes;
import com.apten.parkingvehicle.application.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 주차 조회와 입출차 등록 API 진입점
@RestController
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    //입출차 기록 조회 API-326
    @GetMapping("/api/admin/parking-logs")
    public ResultResponse<PageResponse<ParkingLogListRes>> getParkingLogList(@ModelAttribute ParkingLogListReq request) {
        return ResultResponse.success("입출차 기록 조회 성공", parkingService.getParkingLogList(request));
    }

    //주차 현황 조회 API-327
    @GetMapping("/api/admin/parking/status")
    public ResultResponse<ParkingStatusRes> getParkingStatus(@RequestParam(required = false) Long complexId) {
        return ResultResponse.success("주차 현황 조회 성공", parkingService.getParkingStatus(complexId));
    }

    //층별 주차 관리 조회 API-328
    @GetMapping("/api/admin/parking/floors")
    public ResultResponse<PageResponse<ParkingFloorListRes>> getParkingFloorList(@ModelAttribute ParkingFloorListReq request) {
        return ResultResponse.success("층별 주차 현황 조회 성공", parkingService.getParkingFloorList(request));
    }

    //주차층 등록 API-329
    @PostMapping("/api/admin/parking/floors")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingFloorPostRes> createParkingFloor(
            @RequestParam(required = false) Long complexId,
            @RequestBody ParkingFloorPostReq request
    ) {
        return ResultResponse.success("주차층 등록 성공", parkingService.createParkingFloor(complexId, request));
    }

    //주차층 수정 API-330
    @PatchMapping("/api/admin/parking/floors/{parkingFloorId}")
    public ResultResponse<ParkingFloorPatchRes> updateParkingFloor(
            @PathVariable Long parkingFloorId,
            @RequestParam(required = false) Long complexId,
            @RequestBody ParkingFloorPatchReq request
    ) {
        return ResultResponse.success("주차층 수정 성공", parkingService.updateParkingFloor(parkingFloorId, complexId, request));
    }

    //주차 통계 조회 API-331
    @GetMapping("/api/admin/parking/statistics")
    public ResultResponse<ParkingStatisticsRes> getParkingStatistics(@ModelAttribute ParkingStatisticsReq request) {
        return ResultResponse.success("주차 통계 조회 성공", parkingService.getParkingStatistics(request));
    }

    //입출차 등록 API-332
    @PostMapping("/api/admin/parking-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingLogCreateRes> createParkingLog(@RequestBody ParkingLogCreateReq request) {
        return ResultResponse.success("입출차 등록 성공", parkingService.createParkingLog(request));
    }
}
