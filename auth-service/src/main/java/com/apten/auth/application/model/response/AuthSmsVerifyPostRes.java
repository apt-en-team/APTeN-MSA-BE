package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// SMS 인증번호 검증 응답 DTO
@Getter
@Builder
public class AuthSmsVerifyPostRes {
    // 인증 성공 여부
    private final boolean verified;
}
