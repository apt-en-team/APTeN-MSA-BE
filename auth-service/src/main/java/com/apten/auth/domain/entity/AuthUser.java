package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.AuthProvider;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import com.apten.common.security.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 소셜 로그인 사용자를 auth-service 기준으로 표현한 최소 엔티티
// OAuth2 공급자 정보와 서비스 내부 사용자 식별 정보를 함께 보관하는 기준 모델이다
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser extends BaseEntity {

    // 모든 서비스 엔티티가 같은 PK 규칙을 따르도록 TSID 마커를 함께 둔다
    @Id
    @Tsid
    private Long id;

    // 어떤 소셜 공급자를 통해 로그인했는지 구분한다
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    // 공급자가 내려준 사용자 식별값
    @Column(nullable = false)
    private String providerUserId;

    // 이후 서비스 간 식별과 알림 연동 등에 사용할 이메일
    @Column(nullable = false)
    private String email;

    // gateway와 각 서비스에 전달할 공통 사용자 역할
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
}
