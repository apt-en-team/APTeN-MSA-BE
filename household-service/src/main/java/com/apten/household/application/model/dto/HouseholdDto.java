package com.apten.household.application.model.dto;

import com.apten.household.domain.enums.HouseholdStatus;
import lombok.Builder;
import lombok.Getter;

// household-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 직접 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class HouseholdDto {

    // 세대 식별자
    private final Long id;

    // 세대 동
    private final String building;

    // 세대 호
    private final String unit;

    // 세대 상태
    private final HouseholdStatus status;
}
