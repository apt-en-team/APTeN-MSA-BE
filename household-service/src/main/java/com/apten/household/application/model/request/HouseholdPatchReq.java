package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 정보 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdPatchReq {

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 세대 유형 ID이다.
    private Long typeId;

    // 세대 상태이다.
    private HouseholdStatus status;
}
