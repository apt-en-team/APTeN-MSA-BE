package com.apten.notification.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.model.request.NotificationPushPostReq;
import com.apten.notification.application.model.response.NotificationCleanupRes;
import com.apten.notification.application.model.response.NotificationPostRes;
import com.apten.notification.application.model.response.NotificationPushPostRes;
import com.apten.notification.application.service.NotificationDeliveryService;
import com.apten.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 내부 알림 생성과 푸시 발송, 정리 진입점을 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService notificationService;
    private final NotificationDeliveryService notificationDeliveryService;

    @PostMapping("/internal/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<NotificationPostRes> createNotification(@RequestBody NotificationPostReq request) {
        return ResultResponse.success("내부 알림 생성 성공", notificationService.createNotification(request));
    }

    @PostMapping("/internal/notifications/push")
    public ResultResponse<NotificationPushPostRes> sendPushNotification(@RequestBody NotificationPushPostReq request) {
        return ResultResponse.success("내부 푸시 발송 성공", notificationDeliveryService.sendPushNotification(request));
    }

    @PostMapping("/internal/notifications/cleanup")
    public ResultResponse<NotificationCleanupRes> cleanupOldNotifications() {
        return ResultResponse.success("오래된 알림 정리 실행 성공", notificationService.cleanupOldNotifications());
    }
}
