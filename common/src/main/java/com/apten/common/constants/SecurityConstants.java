package com.apten.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 인증 헤더 최소 상수
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
}
