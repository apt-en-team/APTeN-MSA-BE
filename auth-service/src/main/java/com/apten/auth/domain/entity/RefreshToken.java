package com.apten.auth.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 리프레시 토큰 저장 엔티티
// 재발급과 로그아웃 무효화 기준 데이터를 보관한다
@Entity
@Table(name = "refresh_token")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    // 리프레시 토큰 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 토큰 소유 회원 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 리프레시 토큰 해시
    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    // 디바이스 유형
    @Column(name = "device_type", length = 30)
    private String deviceType;

    // 요청 IP 주소
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // 만료 시각
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 무효화 여부
    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked;

    // 토큰 상태를 갱신할 때 사용한다
    public void apply(
            Long userId,
            String tokenHash,
            String deviceType,
            String ipAddress,
            LocalDateTime expiresAt,
            Boolean isRevoked
    ) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.deviceType = deviceType;
        this.ipAddress = ipAddress;
        this.expiresAt = expiresAt;
        this.isRevoked = isRevoked;
    }
}
