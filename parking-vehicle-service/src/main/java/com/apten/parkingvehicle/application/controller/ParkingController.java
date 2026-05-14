package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.ParkingLogCreateReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogListReq;
import com.apten.parkingvehicle.application.model.request.ParkingStatisticsReq;
import com.apten.parkingvehicle.application.model.request.ParkingZoneListReq;
import com.apten.parkingvehicle.application.model.request.ParkingZonePatchReq;
import com.apten.parkingvehicle.application.model.request.ParkingZonePostReq;
import com.apten.parkingvehicle.application.model.response.*;
import com.apten.parkingvehicle.application.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 주차 조회와 입출차 등록 API 진입점
@RestController
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    //입출차 기록 조회
    @GetMapping("/api/admin/parking-logs")
    public ResultResponse<PageResponse<ParkingLogListRes>> getParkingLogList(
            @ModelAttribute ParkingLogListReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "입출차 기록 조회 성공",
                parkingService.getParkingLogList(request, userRole, complexId, selectedComplexId)
        );
    }

    // 입출차 기록 화면 상단 통계 카드 요약 조회
    @GetMapping("/api/admin/parking-logs/summary")
    public ResultResponse<ParkingLogSummaryRes> getParkingLogSummary(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "입출차 기록 통계 요약 조회 성공",
                parkingService.getParkingLogSummary(userRole, complexId, selectedComplexId)
        );
    }

    //주차 현황 조회
    @GetMapping("/api/admin/parking/status")
    public ResultResponse<ParkingStatusRes> getParkingStatus(
            @RequestParam(required = false) Long complexId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long headerComplexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 현황 조회 성공",
                parkingService.getParkingStatus(userRole, headerComplexId != null ? headerComplexId : complexId, selectedComplexId)
        );
    }

    //주차 구역 목록 조회
    @GetMapping("/api/admin/parking/zones")
    public ResultResponse<PageResponse<ParkingZoneListRes>> getParkingZoneList(
            @ModelAttribute ParkingZoneListReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 구역 목록 조회 성공",
                parkingService.getParkingZoneList(request, userRole, complexId, selectedComplexId)
        );
    }

    //주차 구역 등록
    @PostMapping("/api/admin/parking/zones")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingZonePostRes> createParkingZone(
            @RequestBody ParkingZonePostReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 구역 등록 성공",
                parkingService.createParkingZone(request, userRole, complexId, selectedComplexId)
        );
    }

    //주차 구역 수정
    @PatchMapping("/api/admin/parking/zones/{zoneId}")
    public ResultResponse<ParkingZonePatchRes> updateParkingZone(
            @PathVariable Long zoneId,
            @RequestBody ParkingZonePatchReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 구역 수정 성공",
                parkingService.updateParkingZone(zoneId, request, userRole, complexId, selectedComplexId)
        );
    }

    //주차 구역 삭제
    @DeleteMapping("/api/admin/parking/zones/{zoneId}")
    public ResultResponse<Void> deleteParkingZone(
            @PathVariable Long zoneId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        parkingService.deleteParkingZone(zoneId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("주차 구역 삭제 성공", null);
    }

    //주차 통계 조회
    @GetMapping("/api/admin/parking/statistics")
    public ResultResponse<ParkingStatisticsRes> getParkingStatistics(
            @ModelAttribute ParkingStatisticsReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 통계 조회 성공",
                parkingService.getParkingStatistics(request, userRole, complexId, selectedComplexId)
        );
    }

    //입출차 등록
    @PostMapping("/api/admin/parking-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingLogCreateRes> createParkingLog(
            @RequestBody ParkingLogCreateReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "입출차 등록 성공",
                parkingService.createParkingLog(request, userRole, complexId, selectedComplexId)
        );
    }

    //입주민 주차 현황 조회
    @GetMapping("/api/parking/status")
    public ResultResponse<ResidentParkingStatusRes> getResidentParkingStatus(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId
    ) {
        return ResultResponse.success(
                "주차 현황 조회 성공",
                parkingService.getResidentParkingStatus(userRole, complexId)
        );
    }
}