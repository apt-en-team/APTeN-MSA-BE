package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.notification.domain.enums.NotificationCategory;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 사용자별 알림 카테고리 수신 여부를 저장하는 엔티티
@Entity
@Table(
        name = "notification_setting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_notification_setting_user_category", columnNames = {"user_id", "category"})
        },
        indexes = {
                @Index(name = "idx_notification_setting_user_id", columnList = "user_id"),
                @Index(name = "idx_notification_setting_complex_id", columnList = "complex_id"),
                @Index(name = "idx_notification_setting_category", columnList = "category"),
                @Index(name = "idx_notification_setting_user_enabled", columnList = "user_id, enabled")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting extends BaseEntity {

    // 설정 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 설정 소유 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 사용자 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // RESERVATION, GX 등 알림 설정 단위
    @Column(name = "category", nullable = false, length = 30)
    private NotificationCategory category;

    // false이면 해당 category 알림은 생성 단계에서 제외한다
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public void changeEnabled(boolean enabled) {
        // PATCH 요청에서 전달된 ON/OFF 값을 엔티티에 반영한다
        this.enabled = enabled;
    }
}
