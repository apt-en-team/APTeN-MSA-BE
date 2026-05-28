package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내부 푸시 발송 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPushPostRes {
    // 발송을 시도한 DB 알림 ID
    private Long notificationId;
    // fcm-enabled=false이면 실제 발송 없이 false를 반환한다
    private Boolean sent;
    // PushService 호출이 처리된 시각
    private LocalDateTime sentAt;
}
