package com.apten.auth.application.model.request;

import com.apten.auth.domain.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// auth-service 요청 객체의 공통 시작점으로 사용하는 최소 요청 DTO
// OAuth2 로그인 완료 정보나 토큰 재발급 요청이 들어올 위치를 먼저 정리해 둔다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthBaseRequest {

    // 로그인 공급자 구분값
    private AuthProvider provider;

    // 공급자 사용자 식별값
    private String providerUserId;
}
