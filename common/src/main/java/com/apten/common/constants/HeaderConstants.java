package com.apten.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// gateway가 인증된 사용자 정보를 downstream 서비스로 전달할 때 쓰는 헤더 이름 모음
// auth-service, gateway-service, 각 도메인 서비스가 같은 헤더 키를 보도록 기준을 맞춘다
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    // gateway가 현재 로그인 사용자 ID를 실어 보낼 때 사용하는 헤더
    public static final String X_USER_ID = "X-User-Id";

    // gateway가 현재 로그인 사용자 역할을 실어 보낼 때 사용하는 헤더
    public static final String X_USER_ROLE = "X-User-Role";

    // gateway가 현재 로그인 사용자가 속한 단지 ID를 실어 보낼 때 사용하는 헤더
    public static final String X_COMPLEX_ID = "X-Complex-Id";
}