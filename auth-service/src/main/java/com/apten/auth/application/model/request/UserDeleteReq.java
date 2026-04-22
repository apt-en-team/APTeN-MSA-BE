package com.apten.auth.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 탈퇴 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeleteReq {
    // 본인 확인 비밀번호
    private String password;
}
