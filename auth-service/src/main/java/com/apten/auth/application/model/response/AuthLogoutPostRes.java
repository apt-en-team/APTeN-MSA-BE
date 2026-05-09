package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 로그아웃 응답 DTO
@Getter
@Builder
public class AuthLogoutPostRes {
    // 처리 결과 메시지
    private final String message;
}
