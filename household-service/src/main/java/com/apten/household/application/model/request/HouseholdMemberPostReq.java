package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대원 등록 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberPostReq {

    // 세대에 연결할 사용자 ID이다.
    private Long userId;

    // 세대원 역할이다.
    private HouseholdMemberRole role;
}
