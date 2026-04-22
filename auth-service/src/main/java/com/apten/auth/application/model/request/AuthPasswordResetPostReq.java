package com.apten.auth.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 비밀번호 재설정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthPasswordResetPostReq {
    // 재설정 토큰
    private String token;
    // 새 비밀번호
    private String newPassword;
}
