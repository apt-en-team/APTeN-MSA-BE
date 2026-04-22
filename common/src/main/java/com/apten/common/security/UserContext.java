package com.apten.common.security;

import lombok.Builder;
import lombok.Getter;

// 로그인 사용자 정보
@Getter
@Builder
public class UserContext {

    private final Long userId;
    private final UserRole userRole;
}
