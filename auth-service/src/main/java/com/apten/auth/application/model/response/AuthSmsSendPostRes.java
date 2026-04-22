package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// SMS 인증번호 발송 응답 DTO
@Getter
@Builder
public class AuthSmsSendPostRes {
    // 처리 결과 메시지
    private final String message;
}
