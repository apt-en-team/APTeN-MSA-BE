package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.notification.domain.enums.NotificationChannel;
import com.apten.notification.domain.enums.NotificationDeliveryStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 앱 내 알림과 푸시 발송 결과를 추적하는 엔티티
@Entity
@Table(name = "notification_delivery_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeliveryLog extends BaseEntity {

    // 발송 이력 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 연결된 알림 ID
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    // 사용된 FCM 토큰 ID
    @Column(name = "fcm_token_id")
    private Long fcmTokenId;

    // 발송 채널
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    // 발송 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private NotificationDeliveryStatus deliveryStatus;

    // 실패 사유
    @Column(name = "fail_reason", length = 255)
    private String failReason;

    // 발송 시도 또는 성공 일시
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
