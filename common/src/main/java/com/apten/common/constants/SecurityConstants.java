package com.apten.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 인증 헤더와 토큰 접두사처럼 auth-service와 gateway-service가 함께 쓰는 보안 상수 모음
// 실제 JWT 구현체는 없지만 어떤 헤더 규약을 쓸지는 common에서 먼저 고정해 둔다
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

    // 클라이언트가 JWT를 실어 보낼 때 사용하는 표준 헤더 이름
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Authorization 헤더 안에서 Bearer 토큰을 구분하는 접두사
    public static final String BEARER_PREFIX = "Bearer ";
}
