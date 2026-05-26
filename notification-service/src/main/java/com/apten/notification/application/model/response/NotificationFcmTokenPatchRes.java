package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FCM 토큰 갱신 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFcmTokenPatchRes {
    // 새 토큰으로 갱신 처리 성공 여부
    private Boolean tokenUpdated;
    // 갱신 처리 시각
    private LocalDateTime updatedAt;
}
