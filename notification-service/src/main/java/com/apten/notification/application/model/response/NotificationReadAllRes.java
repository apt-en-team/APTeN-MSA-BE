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
    // 이번 요청에서 읽음 처리된 알림 개수
    private Integer updatedCount;
    // 일괄 읽음 처리 시각
    private LocalDateTime readAt;
}
