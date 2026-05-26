package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 읽음 처리 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadRes {
    // 읽음 처리한 알림 ID
    private Long notificationId;
    // 처리 후 읽음 상태
    private Boolean isRead;
    // 최초 읽음 처리 시각
    private LocalDateTime readAt;
}
