package com.apten.auth.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 이메일 회원가입 응답 DTO
@Getter
@Builder
public class AuthRegisterPostRes {
    // 회원가입 응답도 complexId 기준으로 단지 식별값을 내려준다.
    private final Long complexId;
    // 사용자 ID
    private final Long userId;
    // 사용자 UID
    private final String userUid;
    // 이메일
    private final String email;
    // 이름
    private final String name;
    // 권한
    private final String role;
    // 상태
    private final String status;
    // 생성 시각
    private final LocalDateTime createdAt;
}
