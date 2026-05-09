package com.apten.notification.application.controller;

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
import org.springframework.web.bind.annotation.RestController;

// FCM 토큰 등록과 해제, 갱신을 담당하는 API 진입점
@RestController
@RequiredArgsConstructor
public class NotificationFcmController {

    private final NotificationFcmService notificationFcmService;

    @PostMapping("/api/notifications/fcm-token")
    public ResultResponse<NotificationFcmTokenPostRes> registerFcmToken(@RequestBody NotificationFcmTokenPostReq request) {
        return ResultResponse.success("FCM 토큰 등록 성공", notificationFcmService.registerFcmToken(request));
    }

    @DeleteMapping("/api/notifications/fcm-token")
    public ResultResponse<NotificationFcmTokenDeleteRes> deleteFcmToken(@RequestBody NotificationFcmTokenDeleteReq request) {
        return ResultResponse.success("FCM 토큰 해제 성공", notificationFcmService.deleteFcmToken(request));
    }

    @PatchMapping("/api/notifications/fcm-token")
    public ResultResponse<NotificationFcmTokenPatchRes> updateFcmToken(@RequestBody NotificationFcmTokenPatchReq request) {
        return ResultResponse.success("FCM 토큰 갱신 성공", notificationFcmService.updateFcmToken(request));
    }
}
