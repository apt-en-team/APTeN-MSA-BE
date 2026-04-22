package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내부 알림 생성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPostReq {
    private String receiverUserUid;
    private String apartmentComplexUid;
    private String type;
    private String title;
    private String body;
    private Object payload;
}
