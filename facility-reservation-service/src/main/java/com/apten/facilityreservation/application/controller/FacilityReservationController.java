package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.response.FacilityReservationBaseResponse;
import com.apten.facilityreservation.application.service.FacilityReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// facility-reservation-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 시설 예약 관련 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facility-reservations")
public class FacilityReservationController {

    // 예약 응용 계층 진입점
    private final FacilityReservationService facilityReservationService;

    // 공통 응답 포맷과 기본 라우팅 연결이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<FacilityReservationBaseResponse> getFacilityReservationTemplate() {
        return ResultResponse.success(
                "facility reservation template ready",
                facilityReservationService.getFacilityReservationTemplate()
        );
    }
}
