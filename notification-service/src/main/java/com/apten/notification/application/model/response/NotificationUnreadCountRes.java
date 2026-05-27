package com.apten.notification.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 미읽음 알림 수 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUnreadCountRes {
    // 현재 로그인 사용자의 미읽음 알림 개수
    private Long unreadCount;
}
