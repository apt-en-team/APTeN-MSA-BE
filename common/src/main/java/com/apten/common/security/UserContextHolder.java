package com.apten.common.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// ThreadLocal 사용자 컨텍스트 저장소
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> USER_CONTEXT = new ThreadLocal<>();

    // 사용자 정보 저장
    public static void set(UserContext userContext) {
        USER_CONTEXT.set(userContext);
    }

    // 사용자 정보 조회
    public static UserContext get() {
        return USER_CONTEXT.get();
    }

    // 사용자 정보 제거
    public static void clear() {
        USER_CONTEXT.remove();
    }
}
