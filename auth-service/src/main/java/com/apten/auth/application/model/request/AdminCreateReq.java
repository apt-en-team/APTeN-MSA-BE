package com.apten.auth.application.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// MASTER → MANAGER 생성 / MANAGER → ADMIN 생성 시 공통 요청 DTO
@Getter
public class AdminCreateReq {

    // 생성할 관리자 이메일 — 정확한 이메일 형식 필수
    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "올바른 이메일 형식이 아닙니다"
    )
    private String email;

    // 생성할 관리자 비밀번호 — 8자 이상, 영문+숫자+특수문자 필수
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문+숫자+특수문자를 포함해야 합니다"
    )
    private String password;

    // 생성할 관리자 이름
    @NotBlank
    @Size(max = 50)
    private String name;

    // 담당 단지 ID — MANAGER는 MASTER가 지정, ADMIN은 MANAGER 본인 단지로 강제
    @NotNull
    private Long complexId;
}