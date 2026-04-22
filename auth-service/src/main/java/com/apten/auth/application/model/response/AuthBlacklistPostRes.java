package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 블랙리스트 등록 응답 DTO
@Getter
@Builder
public class AuthBlacklistPostRes {
    // 처리 결과 메시지
    private final String message;
}
