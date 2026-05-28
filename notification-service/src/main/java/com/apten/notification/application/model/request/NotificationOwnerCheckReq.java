package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 소유자 검증 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationOwnerCheckReq {
    // 접근하려는 알림 ID
    private Long notificationId;
    // 현재 로그인 사용자 ID
    private Long loginUserId;
}
