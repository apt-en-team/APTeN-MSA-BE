package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 단지 관리자 배정 해제 응답 DTO이다.
// 단지에서 관리자 소속을 비활성 처리한 결과를 내려줄 때 사용한다.
@Getter
@Builder
public class ComplexAdminDeleteRes {

    // 단지 코드이다.
    private final String code;

    // 해제된 관리자 사용자 ID이다.
    private final Long userId;

    // 현재 활성 여부이다.
    private final Boolean isActive;

    // 해제 시각이다.
    private final LocalDateTime unassignedAt;

    // Auth 내부 계정 삭제 시각이다.
    private final LocalDateTime deletedAt;
}
