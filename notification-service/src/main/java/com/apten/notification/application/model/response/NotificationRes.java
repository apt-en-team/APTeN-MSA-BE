package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRes {
    private Long notificationId;
    private String notificationUid;
    private String title;
    private String body;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
