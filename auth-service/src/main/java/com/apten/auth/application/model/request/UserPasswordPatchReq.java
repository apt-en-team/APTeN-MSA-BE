package com.apten.auth.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 비밀번호 변경 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordPatchReq {
    // 현재 비밀번호
    private String currentPassword;
    // 새 비밀번호
    private String newPassword;
}
