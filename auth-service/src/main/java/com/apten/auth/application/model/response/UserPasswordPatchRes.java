package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 내 비밀번호 변경 응답 DTO
@Getter
@Builder
public class UserPasswordPatchRes {
    // 처리 결과 메시지
    private final String message;
}
