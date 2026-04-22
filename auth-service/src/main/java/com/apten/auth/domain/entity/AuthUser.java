package com.apten.auth.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.security.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 소셜 로그인 사용자를 auth-service 기준으로 표현한 최소 엔티티
// OAuth2 공급자 정보와 서비스 내부 사용자 식별 정보를 함께 보관하는 기준 모델이다
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser extends BaseEntity {

    // auth-service 내부 사용자 식별값
    @Id
    private Long id;

    // kakao, google 같은 OAuth2 공급자 이름
    private String provider;

    // 공급자가 내려준 사용자 식별값
    private String providerUserId;

    // 이후 서비스 간 식별과 알림 연동 등에 사용할 이메일
    private String email;

    // gateway와 각 서비스에 전달할 공통 사용자 역할
    private UserRole role;
}
