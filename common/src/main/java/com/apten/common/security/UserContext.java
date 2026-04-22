package com.apten.common.security;

import lombok.Builder;
import lombok.Getter;

// gateway가 전달한 로그인 사용자 정보를 서비스 내부에서 다루기 위한 공통 컨텍스트 객체
// auth-service의 principal 정보와 도메인 서비스의 현재 사용자 정보 사이를 이어주는 공통 표현이다
@Getter
@Builder
public class UserContext {

    // 현재 요청을 수행하는 로그인 사용자 ID
    private final Long userId;

    // 현재 요청에 적용할 사용자 역할
    private final UserRole userRole;
}
