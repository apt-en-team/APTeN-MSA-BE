package com.apten.notification.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.notification.domain.enums.FcmDeviceType;
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

// 사용자 기기별 FCM 토큰을 저장하는 엔티티
@Entity
@Table(name = "fcm_token")
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
    @Column(name = "token_value", nullable = false, length = 255)
    private String tokenValue;

    // 디바이스 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 30)
    private FcmDeviceType deviceType;

    // 앱 버전 또는 디바이스 정보
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 마지막 사용 일시
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    // 기존 토큰을 새 토큰으로 갱신한다
    public void updateToken(String tokenValue, FcmDeviceType deviceType, String deviceInfo, LocalDateTime updatedAt) {
        this.tokenValue = tokenValue;
        this.deviceType = deviceType;
        this.deviceInfo = deviceInfo;
        this.isActive = true;
        this.lastUsedAt = updatedAt;
    }

    // 토큰을 비활성화한다
    public void deactivate() {
        this.isActive = false;
    }
}
