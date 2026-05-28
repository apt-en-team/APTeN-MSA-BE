package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.notification.domain.enums.NotificationTargetType;
import com.apten.notification.domain.enums.NotificationType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 앱 내 알림 원본 데이터를 저장하는 엔티티
@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_user_id", columnList = "user_id"),
                @Index(name = "idx_notification_complex_id", columnList = "complex_id"),
                @Index(name = "idx_notification_type", columnList = "type"),
                @Index(name = "idx_notification_is_read", columnList = "is_read"),
                @Index(name = "idx_notification_created_at", columnList = "created_at"),
                @Index(name = "idx_notification_user_read", columnList = "user_id, is_read"),
                @Index(name = "idx_notification_user_created", columnList = "user_id, created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    // 알림 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 수신 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 수신 사용자 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 알림 유형
    @Column(name = "type", nullable = false, length = 40)
    private NotificationType type;

    // 관련 대상 유형
    @Column(name = "target_type", nullable = false, length = 40)
    private NotificationTargetType targetType;

    // 관련 대상 ID
    @Column(name = "target_id")
    private Long targetId;

    // 알림 제목
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 알림 본문
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    // 알림 클릭 시 이동할 프론트 경로
    @Column(name = "link_path", length = 255)
    private String linkPath;

    // 도메인별 부가 정보를 문자열 JSON으로 보관한다
    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    // 읽음 여부
    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    // 읽음 일시
    @Column(name = "read_at")
    private LocalDateTime readAt;

    // 알림을 읽음 상태로 바꾼다
    public void markRead(LocalDateTime readAt) {
        // 이미 읽은 알림은 최초 readAt을 유지한다
        if (Boolean.TRUE.equals(this.isRead)) {
            return;
        }
        this.isRead = true;
        this.readAt = readAt;
    }
}
