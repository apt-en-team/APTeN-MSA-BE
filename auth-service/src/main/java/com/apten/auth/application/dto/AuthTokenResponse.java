package com.apten.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

// 인증 토큰 응답 DTO
@Getter
@Builder
public class AuthTokenResponse {

    private final String grantType;
    private final String accessToken;
    private final String refreshToken;
}
