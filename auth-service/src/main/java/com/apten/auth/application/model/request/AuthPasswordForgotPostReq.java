package com.apten.auth.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 비밀번호 재설정 메일 발송 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthPasswordForgotPostReq {
    // 대상 이메일
    private String email;
}
