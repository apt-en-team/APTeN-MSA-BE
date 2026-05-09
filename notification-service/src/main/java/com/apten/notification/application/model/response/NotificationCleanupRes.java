package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 오래된 알림 정리 실행 결과 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCleanupRes {
    private Integer deletedCount;
    private LocalDateTime executedAt;
}
