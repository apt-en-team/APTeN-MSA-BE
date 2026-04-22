package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 비밀번호 재설정 응답 DTO
@Getter
@Builder
public class AuthPasswordResetPostRes {
    // 처리 결과 메시지
    private final String message;
}
