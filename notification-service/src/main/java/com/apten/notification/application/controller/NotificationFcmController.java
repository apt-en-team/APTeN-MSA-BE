package com.apten.notification.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.notification.application.model.request.NotificationFcmTokenDeleteReq;
import com.apten.notification.application.model.request.NotificationFcmTokenPatchReq;
import com.apten.notification.application.model.request.NotificationFcmTokenPostReq;
import com.apten.notification.application.model.response.NotificationFcmTokenDeleteRes;
import com.apten.notification.application.model.response.NotificationFcmTokenPatchRes;
import com.apten.notification.application.model.response.NotificationFcmTokenPostRes;
import com.apten.notification.application.service.NotificationFcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// FCM 토큰 등록과 해제, 갱신을 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
public class NotificationFcmController {

    private final NotificationFcmService notificationFcmService;

    // HTTPS 전환 후 사용할 FCM 토큰을 미리 저장해 두는 준비 API
    @PostMapping("/api/notifications/fcm-tokens")
    public ResultResponse<NotificationFcmTokenPostRes> registerFcmToken(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @RequestBody NotificationFcmTokenPostReq request
    ) {
        return ResultResponse.success("FCM 토큰 등록 성공", notificationFcmService.registerFcmToken(userId, complexId, request));
    }

    // 로그아웃이나 푸시 수신 해제 시 기존 토큰을 비활성화한다
    @DeleteMapping("/api/notifications/fcm-tokens")
    public ResultResponse<NotificationFcmTokenDeleteRes> deleteFcmToken(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestBody NotificationFcmTokenDeleteReq request
    ) {
        return ResultResponse.success("FCM 토큰 해제 성공", notificationFcmService.deleteFcmToken(userId, request));
    }

    // 브라우저가 새 FCM 토큰을 발급하면 기존 토큰을 새 값으로 교체한다
    @PatchMapping("/api/notifications/fcm-tokens")
    public ResultResponse<NotificationFcmTokenPatchRes> updateFcmToken(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @RequestBody NotificationFcmTokenPatchReq request
    ) {
        return ResultResponse.success("FCM 토큰 갱신 성공", notificationFcmService.updateFcmToken(userId, complexId, request));
    }
}
