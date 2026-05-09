package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 토큰 재발급 응답 DTO
@Getter
@Builder
public class AuthTokenRefreshPostRes {
    // 새 액세스 토큰
    private final String accessToken;
    // 새 리프레시 토큰
    private final String refreshToken;
}
