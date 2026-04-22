package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 회원 탈퇴 응답 DTO
@Getter
@Builder
public class UserDeleteRes {
    // 처리 결과 메시지
    private final String message;
}
