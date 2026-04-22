package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.AuthUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// OAuth2 로그인 시 기존 사용자를 다시 찾기 위한 저장소 계약
// 공급자와 공급자 사용자 ID를 기준으로 같은 사용자인지 확인할 때 사용한다
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    // 로그인 공급자 정보로 기존 인증 사용자를 조회한다
    Optional<AuthUser> findByProviderAndProviderUserId(String provider, String providerUserId);
}
