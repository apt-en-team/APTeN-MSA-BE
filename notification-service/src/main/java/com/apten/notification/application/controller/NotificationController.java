package com.apten.notification.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.notification.application.model.response.NotificationBaseResponse;
import com.apten.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// notification-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 알림 목록, 읽음 처리, 발송 이력 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    // 알림 응용 계층 진입점
    private final NotificationService notificationService;

    // 공통 응답 포맷과 기본 라우팅 연결이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<NotificationBaseResponse> getNotificationTemplate() {
        return ResultResponse.success("notification template ready", notificationService.getNotificationTemplate());
    }
}
