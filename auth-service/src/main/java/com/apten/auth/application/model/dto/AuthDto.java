package com.apten.auth.application.model.dto;

import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

// 인증 도메인 내부 전달용 DTO
// 엔티티와 응답 모델을 직접 섞지 않기 위한 중간 객체다
@Getter
@Builder
public class AuthDto {

    // 회원 ID
    private final Long id;

    // 이메일
    private final String email;

    // 이름
    private final String name;

    // 권한
    private final UserRole role;

    // 상태
    private final UserStatus status;

    // 가입 방식
    private final SignupType signupType;
}
