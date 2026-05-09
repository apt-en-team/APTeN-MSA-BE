package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 정보 수정 응답 DTO이다.
@Getter
@Builder
public class HouseholdPatchRes {

    // 세대 ID이다.
    private Long householdId;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 세대 유형 ID이다.
    private Long typeId;

    // 세대 상태이다.
    private HouseholdStatus status;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
