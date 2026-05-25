package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.ParkingLogCreateReq;
import com.apten.parkingvehicle.application.model.response.ParkingLogCreateRes;
import com.apten.parkingvehicle.application.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 정문 LPR 카메라 입출차 등록 진입점이다.
// 게이트웨이 내부 경로 전제로 디바이스 토큰 검증은 두지 않으며, 디바이스 인증은 추후 도입한다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/parking-logs")
public class InternalParkingLogController {

    // 주차 응용 서비스이다.
    private final ParkingService parkingService;

    //번호판 인식 입출차 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ParkingLogCreateRes> createParkingLogByGateRecognition(
            @RequestBody ParkingLogCreateReq request
    ) {
        return ResultResponse.success(
                "입출차 등록 성공",
                parkingService.createParkingLogByGateRecognition(request)
        );
    }
}
