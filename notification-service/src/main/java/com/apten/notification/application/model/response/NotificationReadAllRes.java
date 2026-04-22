package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 전체 읽음 처리 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadAllRes {
    private Integer updatedCount;
    private LocalDateTime readAt;
}
