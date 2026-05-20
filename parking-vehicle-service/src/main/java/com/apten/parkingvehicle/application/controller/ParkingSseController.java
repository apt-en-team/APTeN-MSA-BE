package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.parkingvehicle.application.service.SseSubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 입주민 실시간 자리 점유 SSE 구독 API 진입점
@RestController
@RequiredArgsConstructor
public class ParkingSseController {

    private final SseSubscribeService sseSubscribeService;

    // 입주민 실시간 자리 점유 구독 시작
    @GetMapping(value = "/api/parking/spots/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId
    ) {
        return sseSubscribeService.subscribe(userRole, complexId);
    }
}
