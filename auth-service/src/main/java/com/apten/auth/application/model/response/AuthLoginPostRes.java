package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 이메일 로그인 응답 DTO
@Getter
@Builder
public class AuthLoginPostRes {
    // 액세스 토큰
    private final String accessToken;
    // 리프레시 토큰
    private final String refreshToken;
    // 사용자 ID
    private final Long userId;
    // 사용자 UID
    private final String userUid;
    // 이름
    private final String namegit;
    // 권한
    private final String role;
    // 상태
    private final String status;
    // 동 (USER만 존재)
    private final String building;
    // 호 (USER만 존재)
    private final String unit;
}
