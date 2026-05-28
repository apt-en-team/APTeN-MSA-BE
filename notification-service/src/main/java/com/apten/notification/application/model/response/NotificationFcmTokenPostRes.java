package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FCM 토큰 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFcmTokenPostRes {
    // 토큰 등록 또는 재등록 처리 성공 여부
    private Boolean tokenRegistered;
    // 등록 처리 시각
    private LocalDateTime updatedAt;
}
