package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.SensorMockBulkPostReq;
import com.apten.parkingvehicle.application.model.request.SensorMockPostReq;
import com.apten.parkingvehicle.application.service.SensorMockService;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Mock 시연용 임시 API이다.
@RestController
@RequiredArgsConstructor
public class SensorMockController {

    private final SensorMockService sensorMockService;

    // Mock 센서 초기 상태를 등록한다.
    @PostMapping("/api/admin/parking/sensors/mock/init")
    public ResultResponse<Void> initSensor(@RequestBody SensorMockPostReq request) {
        sensorMockService.initSensor(request);
        return ResultResponse.success("센서 등록 성공", null);
    }

    // Mock 센서 초기 상태 일괄 등록
    @PostMapping("/api/admin/parking/sensors/mock/bulk-init")
    public ResultResponse<Void> initSensorBulk(@RequestBody SensorMockBulkPostReq request) {
        sensorMockService.initSensorBulk(request);
        return ResultResponse.success("센서 일괄 등록 성공", null);
    }

    // Mock 센서 상태를 토글한다.
    @PostMapping("/api/admin/parking/sensors/mock/{sensorCode}/toggle")
    public ResultResponse<Map<String, String>> toggleSensor(@PathVariable String sensorCode) {
        SensorStatus next = sensorMockService.toggleSensor(sensorCode);
        return ResultResponse.success(
                "센서 토글 성공",
                Map.of("sensorCode", sensorCode, "status", next.name())
        );
    }

    // Mock 센서 Hash 전체를 조회한다.
    @GetMapping("/api/admin/parking/sensors/mock/{sensorCode}")
    public ResultResponse<Map<String, String>> getSensor(@PathVariable String sensorCode) {
        return ResultResponse.success("센서 조회 성공", sensorMockService.getSensor(sensorCode));
    }
}
