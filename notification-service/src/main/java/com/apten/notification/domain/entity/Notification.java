package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.notification.domain.enums.NotificationTargetType;
import com.apten.notification.domain.enums.NotificationType;
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

// 앱 내 알림 원본 데이터를 저장하는 엔티티
@Entity
@Table(name = "notification")
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
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    // 관련 대상 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
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

    // 읽음 여부
    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    // 읽음 일시
    @Column(name = "read_at")
    private LocalDateTime readAt;

    // 알림을 읽음 상태로 바꾼다
    public void markRead(LocalDateTime readAt) {
        this.isRead = true;
        this.readAt = readAt;
    }
}
