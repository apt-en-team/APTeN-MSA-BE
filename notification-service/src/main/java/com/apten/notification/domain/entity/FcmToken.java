package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.notification.domain.enums.FcmDeviceType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 사용자 기기별 FCM 토큰을 저장하는 엔티티
@Entity
@Table(
        name = "fcm_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_fcm_token_value", columnNames = "token_value")
        },
        indexes = {
                @Index(name = "idx_fcm_token_user_id", columnList = "user_id"),
                @Index(name = "idx_fcm_token_complex_id", columnList = "complex_id"),
                @Index(name = "idx_fcm_token_is_active", columnList = "is_active"),
                @Index(name = "idx_fcm_token_user_active", columnList = "user_id, is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmToken extends BaseEntity {

    // FCM 토큰 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 토큰 소유 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 사용자 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 실제 FCM 토큰 값
    @Column(name = "token_value", nullable = false, length = 500)
    private String tokenValue;

    // 디바이스 타입
    @Column(name = "device_type", length = 30)
    private FcmDeviceType deviceType;

    // 브라우저별 푸시 동작 차이를 추적하기 위한 값
    @Column(name = "browser_type", length = 50)
    private String browserType;

    // PWA 또는 앱 버전별 토큰 발급 이슈를 확인하기 위한 값
    @Column(name = "app_version", length = 50)
    private String appVersion;

    // OS나 브라우저 상세 정보가 필요할 때 확장해서 저장하는 값
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 마지막 사용 일시
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    // 기존 토큰을 새 토큰으로 갱신한다
    public void updateRegistration(
            Long userId,
            Long complexId,
            String tokenValue,
            FcmDeviceType deviceType,
            String browserType,
            String appVersion,
            String deviceInfo,
            LocalDateTime updatedAt
    ) {
        // 중복 토큰 row를 만들지 않고 현재 소유자와 기기 정보를 최신 상태로 맞춘다
        this.userId = userId;
        this.complexId = complexId;
        this.tokenValue = tokenValue;
        this.deviceType = deviceType;
        this.browserType = browserType;
        this.appVersion = appVersion;
        this.deviceInfo = deviceInfo;
        this.isActive = true;
        this.lastUsedAt = updatedAt;
    }

    // 토큰을 비활성화한다
    public void deactivate() {
        // 이력 보존을 위해 row 삭제 대신 비활성 처리한다
        this.isActive = false;
    }
}
