package com.apten.notification.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.notification.application.model.request.NotificationSearchReq;
import com.apten.notification.application.model.response.NotificationGetPageRes;
import com.apten.notification.application.model.response.NotificationReadAllRes;
import com.apten.notification.application.model.response.NotificationReadRes;
import com.apten.notification.application.model.response.NotificationUnreadCountRes;
import com.apten.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 사용자 알림 조회와 읽음 처리를 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/notifications")
    public ResultResponse<NotificationGetPageRes> getNotificationList(@ModelAttribute NotificationSearchReq request) {
        return ResultResponse.success("알림 목록 조회 성공", notificationService.getNotificationList(request));
    }

    @GetMapping("/api/notifications/unread-count")
    public ResultResponse<NotificationUnreadCountRes> getUnreadCount() {
        return ResultResponse.success("미읽음 알림 수 조회 성공", notificationService.getUnreadCount());
    }

    @PatchMapping("/api/notifications/{notificationUid}/read")
    public ResultResponse<NotificationReadRes> readNotification(@PathVariable String notificationUid) {
        return ResultResponse.success("알림 읽음 처리 성공", notificationService.readNotification(notificationUid));
    }

    @PatchMapping("/api/notifications/read-all")
    public ResultResponse<NotificationReadAllRes> readAllNotifications() {
        return ResultResponse.success("전체 읽음 처리 성공", notificationService.readAllNotifications());
    }
}
