package com.apten.auth.application;

import com.apten.auth.application.dto.AuthTokenResponse;
import com.apten.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 인증 서비스 뼈대
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    // 토큰 응답 형식 샘플 생성
    public AuthTokenResponse createSampleTokenResponse(String authorizationHeader) {
        return AuthTokenResponse.builder()
                .grantType(jwtTokenProvider.resolveTokenType(authorizationHeader))
                .accessToken(null)
                .refreshToken(null)
                .build();
    }
}
