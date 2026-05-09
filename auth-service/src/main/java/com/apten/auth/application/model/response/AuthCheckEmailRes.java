package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 이메일 중복 확인 응답 DTO
@Getter
@Builder
public class AuthCheckEmailRes {
    // 중복 여부
    private final boolean isDuplicate;
}
