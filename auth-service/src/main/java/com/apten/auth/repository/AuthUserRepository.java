package com.apten.auth.repository;

import com.apten.auth.entity.AuthUser;
import java.util.Optional;

// OAuth2 로그인 시 기존 사용자를 다시 찾기 위한 저장소 계약
// 공급자와 공급자 사용자 ID를 기준으로 같은 사용자인지 확인할 때 사용한다
public interface AuthUserRepository {

    // 로그인 공급자 정보로 기존 인증 사용자를 조회한다
    Optional<AuthUser> findByProviderAndProviderUserId(String provider, String providerUserId);
}
