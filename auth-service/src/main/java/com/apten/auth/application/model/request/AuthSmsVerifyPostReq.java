package com.apten.auth.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// SMS 인증번호 검증 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSmsVerifyPostReq {
    // 휴대폰 번호
    private String phone;
    // 입력한 인증 코드
    private String code;
}
