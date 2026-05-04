package com.apten.auth.application.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

//추가 단지 서비스 내부 연동용 관리자 생성 요청 DTO
@Getter
public class InternalAdminCreateReq {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotNull
    private Long complexId;

    @NotBlank
    private String adminRole;
}
