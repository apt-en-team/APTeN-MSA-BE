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
    private Boolean tokenUpdated;
    private LocalDateTime updatedAt;
}
