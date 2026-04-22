package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 로그인 성공 후 클라이언트에 내려줄 토큰 응답 객체
// access token과 refresh token, 현재 로그인 사용자를 함께 묶어 반환할 때 사용한다
@Getter
@Builder
public class AuthTokenResponse {

    // 현재 토큰이 어떤 방식으로 전달되는지 나타낸다
    private final String grantType;

    // 이후 gateway가 검증 대상으로 사용할 access token 값
    private final String accessToken;

    // 재발급 흐름에서 사용할 refresh token 값
    private final String refreshToken;

    // 현재 로그인한 사용자 식별자
    private final Long userId;

    // 현재 로그인한 사용자 역할
    private final String userRole;
}
