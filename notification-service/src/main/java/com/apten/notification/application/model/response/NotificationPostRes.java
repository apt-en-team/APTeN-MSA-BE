package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내부 알림 생성 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPostRes {
    // 생성된 DB 알림 ID, 설정 OFF로 생성 제외되면 null일 수 있다
    private Long notificationId;
    // 생성 요청 대상 사용자 ID
    private Long receiverUserId;
    // 생성 시각 또는 생성 제외 처리 시각
    private LocalDateTime createdAt;
}
