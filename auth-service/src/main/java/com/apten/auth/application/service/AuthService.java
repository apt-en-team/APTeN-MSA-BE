package com.apten.auth.application.service;

import com.apten.auth.application.mapper.AuthUserMapper;
import com.apten.auth.application.model.response.AuthTokenResponse;
import com.apten.auth.domain.entity.AuthUser;
import com.apten.auth.domain.enums.AuthProvider;
import com.apten.auth.domain.repository.AuthUserRepository;
import com.apten.auth.infrastructure.mapper.AuthQueryMapper;
import com.apten.auth.security.JwtTokenProvider;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// OAuth2 로그인 성공 이후 토큰 응답을 조합하는 서비스
// 컨트롤러나 성공 핸들러가 직접 저장소와 JWT 발급을 다루지 않도록 중간 흐름을 모아둔다
@Service
@RequiredArgsConstructor
public class AuthService {

    // 기본 사용자 저장과 조회는 JPA Repository가 맡는다
    private final AuthUserRepository authUserRepository;

    // 복잡한 인증 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<AuthQueryMapper> authQueryMapperProvider;

    // 로그인 사용자 정보를 공통 컨텍스트와 응답 객체로 바꾸는 변환기
    private final AuthUserMapper authUserMapper;

    // 실제 access token과 refresh token 생성 담당
    private final JwtTokenProvider jwtTokenProvider;

    // 헤더를 받아 auth-service가 어떤 토큰 응답을 줄지 확인하는 샘플 응답을 만든다
    public AuthTokenResponse createSampleTokenResponse(String authorizationHeader) {
        UserContext sampleUserContext = UserContext.builder()
                .userId(1L)
                .userRole(UserRole.RESIDENT)
                .build();

        return authUserMapper.toTokenResponse(
                jwtTokenProvider.resolveTokenType(authorizationHeader),
                null,
                null,
                sampleUserContext
        );
    }

    // OAuth2 로그인으로 확보한 사용자 정보를 기준으로 실제 토큰 응답을 만든다
    // 성공 핸들러는 이 메서드를 호출해 JWT 발급 책임을 서비스 계층으로 넘긴다
    public AuthTokenResponse issueTokenResponse(UserPrincipal userPrincipal) {
        UserContext userContext = authUserMapper.toUserContext(userPrincipal);
        String grantType = SecurityConstants.BEARER_PREFIX.trim();
        String accessToken = jwtTokenProvider.issueAccessToken(userContext);
        String refreshToken = jwtTokenProvider.issueRefreshToken(userPrincipal.getUserId());

        return authUserMapper.toTokenResponse(grantType, accessToken, refreshToken, userContext);
    }

    // auth-service 기본 구조와 응답 포맷을 확인할 수 있는 최소 사용자 응답을 만든다
    public AuthTokenResponse getAuthTemplate() {
        AuthUser authUser = AuthUser.builder()
                .id(1L)
                .provider(AuthProvider.KAKAO)
                .providerUserId("oauth2-user-1")
                .email("auth-template@apten.com")
                .role(UserRole.RESIDENT)
                .build();

        UserContext userContext = UserContext.builder()
                .userId(authUser.getId())
                .userRole(authUser.getRole())
                .build();

        return authUserMapper.toTokenResponse("Bearer", null, null, userContext);
    }
}
