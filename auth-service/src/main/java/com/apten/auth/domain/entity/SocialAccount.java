package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.SocialLinkStatus;
import com.apten.auth.domain.enums.SocialProvider;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 소셜 로그인 연동 정보를 저장하는 엔티티
// 제공자 계정과 내부 회원 계정을 연결할 때 사용한다
@Entity
@Table(name = "social_account")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccount extends BaseEntity {

    // 소셜 계정 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 연결된 회원 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 제공자 종류는 converter를 통해 DB에는 code로 저장된다
    @Column(name = "provider", nullable = false, length = 20)
    private SocialProvider provider;

    // 제공자 사용자 식별값
    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    // 제공자 이메일
    @Column(name = "provider_email", length = 100)
    private String providerEmail;

    // 연동 상태는 converter를 통해 DB에는 code로 저장된다
    @Column(name = "link_status", nullable = false, length = 20)
    private SocialLinkStatus linkStatus;

    // 소셜 연동 정보를 갱신할 때 사용한다
    public void apply(
            Long userId,
            SocialProvider provider,
            String providerUserId,
            String providerEmail,
            SocialLinkStatus linkStatus
    ) {
        this.userId = userId;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.providerEmail = providerEmail;
        this.linkStatus = linkStatus;
    }
}
