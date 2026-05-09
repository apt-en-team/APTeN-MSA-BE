package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 마스터 등록 응답 DTO이다.
@Getter
@Builder
public class HouseholdCreateRes {

    // 생성된 세대 ID이다.
    private Long householdId;

    // 단지 ID이다.
    private Long complexId;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 세대 상태이다.
    private HouseholdStatus status;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
