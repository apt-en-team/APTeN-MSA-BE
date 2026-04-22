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

// 비밀번호 재설정 토큰 엔티티
// 메일 링크로 전달된 토큰 사용 여부를 관리한다
@Entity
@Table(name = "password_reset_token")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken extends BaseEntity {

    // 재설정 토큰 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 대상 회원 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 재설정 토큰 해시
    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    // 만료 시각
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 사용 여부
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    // 토큰 상태를 갱신할 때 사용한다
    public void apply(Long userId, String tokenHash, LocalDateTime expiresAt, Boolean isUsed) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.isUsed = isUsed;
    }
}
