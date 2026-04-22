package com.apten.auth.repository;

import com.apten.auth.entity.AuthUser;
import java.util.Optional;

// 인증 사용자 저장소 계약
public interface AuthUserRepository {

    Optional<AuthUser> findByProviderAndProviderUserId(String provider, String providerUserId);
}
