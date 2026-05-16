package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.ParkingSensorBulkPostReq;
import com.apten.parkingvehicle.application.model.request.ParkingSensorPatchReq;
import com.apten.parkingvehicle.application.model.request.ParkingSensorPostReq;
import com.apten.parkingvehicle.application.model.response.ParkingSensorBulkPostRes;
import com.apten.parkingvehicle.application.model.response.ParkingSensorRes;
import com.apten.parkingvehicle.application.service.ParkingSensorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 주차 센서 CRUD와 일괄 등록 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminParkingSensorController {

    private final ParkingSensorService parkingSensorService;

    // 주차 센서 단건 등록
    @PostMapping("/api/admin/parking/sensors")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingSensorRes> createSensor(
            @RequestBody ParkingSensorPostReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 센서 등록 성공",
                parkingSensorService.createSensor(request, userRole, complexId, selectedComplexId)
        );
    }

    // 주차 센서 일괄 등록
    @PostMapping("/api/admin/parking/sensors/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingSensorBulkPostRes> createSensorsBulk(
            @RequestBody ParkingSensorBulkPostReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 센서 일괄 등록 성공",
                parkingSensorService.createSensorsBulk(request, userRole, complexId, selectedComplexId)
        );
    }

    // 주차 구역 기준 주차 센서 목록 조회
    @GetMapping("/api/admin/parking/sensors")
    public ResultResponse<List<ParkingSensorRes>> getSensorList(
            @RequestParam Long zoneId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 센서 목록 조회 성공",
                parkingSensorService.getSensorList(zoneId, userRole, complexId, selectedComplexId)
        );
    }

    // 주차 센서 단건 조회
    @GetMapping("/api/admin/parking/sensors/{sensorId}")
    public ResultResponse<ParkingSensorRes> getSensor(
            @PathVariable Long sensorId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 센서 단건 조회 성공",
                parkingSensorService.getSensor(sensorId, userRole, complexId, selectedComplexId)
        );
    }

    // 주차 센서 수정
    @PatchMapping("/api/admin/parking/sensors/{sensorId}")
    public ResultResponse<ParkingSensorRes> updateSensor(
            @PathVariable Long sensorId,
            @RequestBody ParkingSensorPatchReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 센서 수정 성공",
                parkingSensorService.updateSensor(sensorId, request, userRole, complexId, selectedComplexId)
        );
    }

    // 주차 센서 소프트 삭제
    @DeleteMapping("/api/admin/parking/sensors/{sensorId}")
    public ResultResponse<Void> deleteSensor(
            @PathVariable Long sensorId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        parkingSensorService.deleteSensor(sensorId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("주차 센서 삭제 성공", null);
    }
}
