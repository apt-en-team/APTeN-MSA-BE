package com.apten.notification.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FCM 토큰 삭제 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFcmTokenDeleteRes {
    private Boolean tokenDeleted;
}
