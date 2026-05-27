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

    // 본인 userId 기준으로 알림 목록을 최신순 조회한다
    @GetMapping
    public ResultResponse<NotificationGetPageRes> getNotificationList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @ModelAttribute NotificationSearchReq request
    ) {
        return ResultResponse.success("알림 목록 조회 성공", notificationService.getNotificationList(userId, request));
    }

    // 헤더/드롭다운 배지에서 사용할 미읽음 수만 빠르게 조회한다
    @GetMapping("/unread-count")
    public ResultResponse<NotificationUnreadCountRes> getUnreadCount(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId
    ) {
        return ResultResponse.success("미읽음 알림 수 조회 성공", notificationService.getUnreadCount(userId));
    }

    // 단건 읽음 처리는 서비스에서 본인 알림인지 한 번 더 검증한다
    @PatchMapping("/{notificationId}/read")
    public ResultResponse<NotificationReadRes> readNotification(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @PathVariable Long notificationId
    ) {
        return ResultResponse.success("알림 읽음 처리 성공", notificationService.readNotification(userId, notificationId));
    }

    // 전체 읽음은 현재 로그인 사용자의 미읽음 알림만 대상으로 한다
    @PatchMapping("/read-all")
    public ResultResponse<NotificationReadAllRes> readAllNotifications(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId
    ) {
        return ResultResponse.success("전체 읽음 처리 성공", notificationService.readAllNotifications(userId));
    }

    // 설정 조회 시 빠진 category row는 기본 ON으로 생성해서 반환한다
    @GetMapping("/settings")
    public ResultResponse<NotificationSettingGetRes> getSettings(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId
    ) {
        return ResultResponse.success("알림 설정 조회 성공", notificationSettingService.getSettings(userId, complexId));
    }

    // category별 ON/OFF 변경값을 저장하고 변경 후 전체 설정 상태를 반환한다
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
