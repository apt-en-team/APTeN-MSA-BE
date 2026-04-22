package com.apten.auth.application;

import com.apten.auth.application.dto.AuthTokenResponse;
import com.apten.auth.mapper.AuthUserMapper;
import com.apten.auth.security.JwtTokenProvider;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// OAuth2 로그인 성공 이후 토큰 응답을 조합하는 서비스
// 컨트롤러나 성공 핸들러가 직접 JWT를 만들지 않도록 중간 흐름을 모아둔다
@Service
@RequiredArgsConstructor
public class AuthService {

    // 로그인 사용자 정보를 공통 컨텍스트로 바꾸는 변환기
    private final AuthUserMapper authUserMapper;

    // 실제 access token과 refresh token 생성 담당
    private final JwtTokenProvider jwtTokenProvider;

    // 헤더를 받아 grant type 표현을 맞춘 응답 형태를 미리 확인할 때 사용
    // 초기에 프런트와 응답 형식을 맞추거나 토큰 포맷을 점검할 때 읽기 좋은 메서드다
    public AuthTokenResponse createSampleTokenResponse(String authorizationHeader) {
        return AuthTokenResponse.builder()
                .grantType(jwtTokenProvider.resolveTokenType(authorizationHeader))
                .accessToken(null)
                .refreshToken(null)
                .build();
    }

    // OAuth2 로그인으로 확보한 사용자 정보를 기준으로 실제 토큰 응답을 만든다
    // 성공 핸들러는 이 메서드를 호출해 JWT 발급 책임을 서비스 계층으로 넘긴다
    public AuthTokenResponse issueTokenResponse(UserPrincipal userPrincipal) {
        UserContext userContext = authUserMapper.toUserContext(userPrincipal);

        return AuthTokenResponse.builder()
                .grantType(SecurityConstants.BEARER_PREFIX.trim())
                .accessToken(jwtTokenProvider.issueAccessToken(userContext))
                .refreshToken(jwtTokenProvider.issueRefreshToken(userPrincipal.getUserId()))
                .build();
    }
}
