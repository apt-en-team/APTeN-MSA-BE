package com.apten.auth.application.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

// 이메일 중복 확인 응답 DTO
@Getter
@Builder
public class AuthCheckEmailRes {
    // boolean + is 접두사는 Jackson이 "duplicate"로 직렬화하므로 키 이름을 명시한다
    @JsonProperty("isDuplicate")
    private final boolean isDuplicate;
}
