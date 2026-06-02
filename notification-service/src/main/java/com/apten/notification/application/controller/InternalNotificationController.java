package com.apten.notification.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.notification.application.model.request.NotificationAdminBroadcastPostReq;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.model.request.NotificationPushPostReq;
import com.apten.notification.application.model.response.NotificationAdminBroadcastPostRes;
import com.apten.notification.application.model.response.NotificationCleanupRes;
import com.apten.notification.application.model.response.NotificationPostRes;
import com.apten.notification.application.model.response.NotificationPushPostRes;
import com.apten.notification.application.service.NotificationDeliveryService;
import com.apten.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// 내부 알림 생성과 푸시 발송, 정리 진입점을 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/notifications")
public class InternalNotificationController {

    private final NotificationService notificationService;
    private final NotificationDeliveryService notificationDeliveryService;

    // 내부 알림 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<NotificationPostRes> createNotification(@RequestBody NotificationPostReq request) {
        // 시설예약/GX 알림은 이 endpoint에서 notification row 저장 흐름으로 들어온다
        return ResultResponse.success("내부 알림 생성 성공", notificationService.createNotification(request));
    }

    // 내부 푸시 발송
    @PostMapping("/push")
    public ResultResponse<NotificationPushPostRes> sendPushNotification(@RequestBody NotificationPushPostReq request) {
        // 일반 GX 승인 흐름에서 자동 호출되는 API가 아니라, 별도 푸시 재발송/내부 발송용 진입점이다
        return ResultResponse.success("내부 푸시 발송 성공", notificationDeliveryService.sendPushNotification(request));
    }

    // 관리자 broadcast 알림 생성
    @PostMapping("/admin-broadcast")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<NotificationAdminBroadcastPostRes> createAdminBroadcastNotification(
            @RequestBody NotificationAdminBroadcastPostReq request) {
        // complexId 기준 ACTIVE ADMIN/MANAGER 전체에게 각각 알림을 생성한다
        return ResultResponse.success("관리자 broadcast 알림 생성 성공",
                notificationService.createAdminBroadcastNotification(request));
    }

    // 오래된 알림 수동 정리
    @PostMapping("/cleanup")
    public ResultResponse<NotificationCleanupRes> cleanupOldNotifications() {
        return ResultResponse.success("오래된 알림 정리 실행 성공", notificationService.cleanupOldNotifications());
    }
}
