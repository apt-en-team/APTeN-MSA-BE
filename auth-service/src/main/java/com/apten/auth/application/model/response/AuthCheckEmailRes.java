package com.apten.auth.application.model.response;

import lombok.Builder;

// 이메일 중복 확인 응답 DTO
// Boolean 래퍼 타입 사용 → Lombok이 getIsDuplicate()를 생성 → Jackson이 "isDuplicate"로 직렬화
@Builder
public class AuthCheckEmailRes {
    private final Boolean isDuplicate;

    public Boolean getIsDuplicate() {
        return isDuplicate;
    }
}
