package com.apten.facilityreservation.application.model.dto;

import com.apten.common.security.UserRole;
import lombok.Builder;
import lombok.Getter;

// 시설예약 서비스가 현재 요청 사용자와 단지 컨텍스트를 함께 전달할 때 사용하는 DTO이다.
@Getter
@Builder
public class FacilityRequestContext {

    // 현재 요청 사용자 ID이다.
    private final Long userId;

    // 현재 요청 사용자 역할이다.
    private final UserRole userRole;

    // 현재 요청에 적용할 단지 ID이다.
    private final Long complexId;
}
