package com.apten.auth.application.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    // 현재 비밀번호 — 본인 확인용
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    // 새 비밀번호 — 8자 이상, 영문/숫자/특수문자 포함
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword;
}