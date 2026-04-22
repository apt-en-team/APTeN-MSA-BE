package com.apten.notification.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 소유자 검증 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationOwnerCheckRes {
    private Boolean allowed;
}
