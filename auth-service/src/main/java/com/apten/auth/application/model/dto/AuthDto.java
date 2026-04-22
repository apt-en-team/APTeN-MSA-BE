package com.apten.auth.application.model.dto;

import com.apten.auth.domain.enums.AuthProvider;
import com.apten.common.security.UserRole;
import lombok.Builder;
import lombok.Getter;

// auth-service 내부 계층 간 전달에 사용할 최소 DTO
// 컨트롤러 응답 모델과 엔티티를 바로 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class AuthDto {

    // auth-service 내부 사용자 식별자
    private final Long id;

    // 로그인 공급자
    private final AuthProvider provider;

    // 사용자 이메일
    private final String email;

    // 공통 사용자 역할
    private final UserRole role;
}
