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
import org.springframework.web.bind.annotation.*;

// FCM 토큰 등록과 해제, 갱신을 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationFcmController {

    private final NotificationFcmService notificationFcmService;

    // FCM 토큰 등록
    @PostMapping("/fcm-tokens")
    public ResultResponse<NotificationFcmTokenPostRes> registerFcmToken(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestBody NotificationFcmTokenPostReq request
    ) {
        return ResultResponse.success("FCM 토큰 등록 성공", notificationFcmService.registerFcmToken(userId, complexId, request));
    }

    // FCM 토큰 해제
    @DeleteMapping("/fcm-tokens")
    public ResultResponse<NotificationFcmTokenDeleteRes> deleteFcmToken(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestBody NotificationFcmTokenDeleteReq request
    ) {
        return ResultResponse.success("FCM 토큰 해제 성공", notificationFcmService.deleteFcmToken(userId, request));
    }

    // FCM 토큰 갱신
    @PatchMapping("/fcm-tokens")
    public ResultResponse<NotificationFcmTokenPatchRes> updateFcmToken(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestBody NotificationFcmTokenPatchReq request
    ) {
        return ResultResponse.success("FCM 토큰 갱신 성공", notificationFcmService.updateFcmToken(userId, complexId, request));
    }
}
