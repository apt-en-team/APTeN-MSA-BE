package com.apten.household.application.model.dto;

import com.apten.common.security.UserRole;
import lombok.Builder;
import lombok.Getter;

// household-service가 Gateway 헤더에서 해석한 현재 요청 컨텍스트이다.
@Getter
@Builder
public class HouseholdRequestContext {

    // 현재 요청 사용자 ID이다.
    private Long userId;

    // 현재 요청 사용자 역할이다.
    private UserRole userRole;

    // 현재 요청 단지 ID이다.
    private Long complexId;
}
