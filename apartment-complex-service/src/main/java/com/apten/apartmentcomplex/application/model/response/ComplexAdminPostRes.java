package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 단지 소속 지정 응답 DTO이다.
// 단지에 배정된 관리자 정보를 내려줄 때 사용한다.
@Getter
@Builder
public class ComplexAdminPostRes {

    // 단지 코드이다.
    private final String code;

    // 관리자 사용자 ID이다.
    private final Long userId;

    // 관리자 이름이다.
    private final String name;

    // 관리자 이메일이다.
    private final String email;

    // 단지 내 관리자 역할이다.
    private final String adminRole;

    // 현재 활성 여부이다.
    private final Boolean isActive;

    // 배정 시각이다.
    private final LocalDateTime assignedAt;
}
