package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내부 푸시 발송 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPushPostReq {
    private String notificationUid;
}
