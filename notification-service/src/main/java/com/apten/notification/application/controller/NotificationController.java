package com.apten.notification.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.notification.application.model.request.NotificationSearchReq;
import com.apten.notification.application.model.request.NotificationSettingPatchReq;
import com.apten.notification.application.model.response.NotificationGetPageRes;
import com.apten.notification.application.model.response.NotificationReadAllRes;
import com.apten.notification.application.model.response.NotificationReadRes;
import com.apten.notification.application.model.response.NotificationSettingGetRes;
import com.apten.notification.application.model.response.NotificationSettingPatchRes;
import com.apten.notification.application.model.response.NotificationUnreadCountRes;
import com.apten.notification.application.service.NotificationSettingService;
import com.apten.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 사용자 알림 조회와 읽음 처리를 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    // 알림 목록 조회
    @GetMapping
    public ResultResponse<NotificationGetPageRes> getNotificationList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @ModelAttribute NotificationSearchReq request
    ) {
        return ResultResponse.success("알림 목록 조회 성공", notificationService.getNotificationList(userId, request));
    }

    // 미읽음 알림 수 조회
    @GetMapping("/unread-count")
    public ResultResponse<NotificationUnreadCountRes> getUnreadCount(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId
    ) {
        return ResultResponse.success("미읽음 알림 수 조회 성공", notificationService.getUnreadCount(userId));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResultResponse<NotificationReadRes> readNotification(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @PathVariable Long notificationId
    ) {
        return ResultResponse.success("알림 읽음 처리 성공", notificationService.readNotification(userId, notificationId));
    }

    // 전체 알림 읽음 처리
    @PatchMapping("/read-all")
    public ResultResponse<NotificationReadAllRes> readAllNotifications(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId
    ) {
        return ResultResponse.success("전체 읽음 처리 성공", notificationService.readAllNotifications(userId));
    }

    // 알림 설정 조회
    @GetMapping("/settings")
    public ResultResponse<NotificationSettingGetRes> getSettings(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId
    ) {
        return ResultResponse.success("알림 설정 조회 성공", notificationSettingService.getSettings(userId, complexId));
    }

    // 알림 설정 변경
    @PatchMapping("/settings")
    public ResultResponse<NotificationSettingPatchRes> updateSettings(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestBody NotificationSettingPatchReq request
    ) {
        return ResultResponse.success(
                "알림 설정 변경 성공",
                notificationSettingService.updateSettings(userId, complexId, request)
        );
    }
}
