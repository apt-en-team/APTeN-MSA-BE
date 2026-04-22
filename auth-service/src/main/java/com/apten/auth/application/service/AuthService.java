package com.apten.auth.application.service;

import com.apten.auth.application.dto.AuthTokenResponse;
import com.apten.auth.mapper.AuthUserMapper;
import com.apten.auth.security.JwtTokenProvider;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 인증 서비스 뼈대
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserMapper authUserMapper;
    private final JwtTokenProvider jwtTokenProvider;

    // 토큰 응답 형식 샘플 생성
    public AuthTokenResponse createSampleTokenResponse(String authorizationHeader) {
        return AuthTokenResponse.builder()
                .grantType(jwtTokenProvider.resolveTokenType(authorizationHeader))
                .accessToken(null)
                .refreshToken(null)
                .build();
    }

    // 로그인 사용자 기준 토큰 발급
    public AuthTokenResponse issueTokenResponse(UserPrincipal userPrincipal) {
        UserContext userContext = authUserMapper.toUserContext(userPrincipal);

        return AuthTokenResponse.builder()
                .grantType(SecurityConstants.BEARER_PREFIX.trim())
                .accessToken(jwtTokenProvider.issueAccessToken(userContext))
                .refreshToken(jwtTokenProvider.issueRefreshToken(userPrincipal.getUserId()))
                .build();
    }
}
