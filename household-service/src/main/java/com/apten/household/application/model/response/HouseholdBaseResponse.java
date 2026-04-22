package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdStatus;
import lombok.Builder;
import lombok.Getter;

// household-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 내부에 담길 세대 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class HouseholdBaseResponse {

    // 세대 식별자
    private final Long id;

    // 세대 동
    private final String building;

    // 세대 호
    private final String unit;

    // 세대 상태
    private final HouseholdStatus status;
}
