package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.SmsVerifyType;
import com.apten.common.entity.BaseEntity;
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

// SMS 인증 정보를 저장하는 엔티티
// 회원가입과 비밀번호 재설정 인증 흐름에서 사용한다
@Entity
@Table(name = "sms_verification")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerification extends BaseEntity {

    // SMS 인증 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 인증 대상 휴대폰 번호
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // 인증 코드 해시
    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    // 인증 목적
    @Enumerated(EnumType.STRING)
    @Column(name = "verify_type", nullable = false, length = 20)
    private SmsVerifyType verifyType;

    // 만료 시각
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 인증 완료 여부
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    // 인증 정보를 갱신할 때 사용한다
    public void apply(
            String phone,
            String codeHash,
            SmsVerifyType verifyType,
            LocalDateTime expiresAt,
            Boolean isVerified
    ) {
        this.phone = phone;
        this.codeHash = codeHash;
        this.verifyType = verifyType;
        this.expiresAt = expiresAt;
        this.isVerified = isVerified;
    }
}
