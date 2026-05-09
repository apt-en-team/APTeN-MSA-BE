package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FCM 토큰 삭제 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFcmTokenDeleteReq {
    private String token;
}
