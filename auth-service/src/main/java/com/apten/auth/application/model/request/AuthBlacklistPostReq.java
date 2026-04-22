package com.apten.auth.application.model.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 블랙리스트 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthBlacklistPostReq {
    // 블랙리스트 대상 토큰
    private String token;
    // 토큰 만료 시각
    private LocalDateTime expiresAt;
}
