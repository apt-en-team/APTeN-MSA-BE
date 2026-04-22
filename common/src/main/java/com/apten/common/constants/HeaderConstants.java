package com.apten.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// 사용자 전달 헤더 상수
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    public static final String X_USER_ID = "X-User-Id";
    public static final String X_USER_ROLE = "X-User-Role";
}
