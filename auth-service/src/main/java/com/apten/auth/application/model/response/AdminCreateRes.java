package com.apten.auth.application.model.response;

import com.apten.auth.domain.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

// MANAGER / ADMIN 계정 생성 응답 DTO
@Getter
@Builder
public class AdminCreateRes {

    // 생성된 계정 ID
    private Long userId;

    // 생성된 계정 이메일
    private String email;

    // 생성된 계정 이름
    private String name;

    //추가 관리자 계정 생성 응답에도 연락처를 포함한다.
    private String phone;

    // 부여된 역할 — MANAGER 또는 ADMIN
    private String role;

    // 담당 단지 ID
    private Long complexId;
}
