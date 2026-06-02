package com.apten.facilityreservation.infrastructure.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// notification-service /internal/notifications/admin-broadcast 요청 DTO
// complexId 기준 ACTIVE ADMIN/MANAGER 전체에게 알림을 생성할 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAdminBroadcastReq {
    private Long complexId;
    private String type;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String linkPath;
    private String payloadJson;
}
