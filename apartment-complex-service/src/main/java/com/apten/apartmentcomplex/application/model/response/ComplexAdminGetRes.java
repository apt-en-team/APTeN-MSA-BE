package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 단지 관리자 목록 응답 DTO이다.
// 특정 단지에 배정된 관리자 목록을 내려줄 때 사용한다.
@Getter
@Builder
public class ComplexAdminGetRes {

    // 관리자 사용자 ID이다.
    private final Long userId;

    // 관리자 이름이다.
    private final String name;

    // 현재 배정 활성 여부이다.
    private final Boolean isActive;

    // 단지에 배정된 시각이다.
    private final LocalDateTime assignedAt;

    // 단지에서 해제된 시각이다.
    private final LocalDateTime unassignedAt;
}
