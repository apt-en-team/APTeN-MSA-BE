package com.apten.notification.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// notification-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 알림 목록, 알림 읽음 처리 같은 API는 이후 이 계층에서 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
}
