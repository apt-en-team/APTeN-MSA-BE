package com.apten.auth.application.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이메일 회원가입 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegisterPostReq {

    // 선택한 단지 UID
    @NotBlank(message = "단지를 선택해주세요.")
    private String apartmentComplexUid;

    // 이메일
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 비밀번호 — 8자 이상, 영문+숫자+특수문자 필수 (FR-011)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    // 이름
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    // 연락처
    @NotBlank(message = "연락처를 입력해주세요.")
    private String phone;

    // 생년월일
    @NotNull(message = "생년월일을 입력해주세요.")
    private LocalDate birthDate;

    // 동 정보
    @NotBlank(message = "동을 입력해주세요.")
    private String dong;

    // 호 정보
    @NotBlank(message = "호를 입력해주세요.")
    private String ho;

    // SMS 인증 코드
    @NotBlank(message = "SMS 인증코드를 입력해주세요.")
    private String authCode;
}