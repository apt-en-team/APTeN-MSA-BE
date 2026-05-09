package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.enums.HouseholdStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 내 세대 정보 조회 응답 DTO이다.
@Getter
@Builder
public class MyHouseholdRes {

    // 세대 ID이다.
    private Long householdId;

    // 단지 ID이다.
    private Long complexId;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 세대 상태이다.
    private HouseholdStatus status;

    // 세대원 목록이다.
    private List<MemberItem> members;

    // 세대원 한 건이다.
    @Getter
    @Builder
    public static class MemberItem {
        private Long userId;
        private String name;
        private String phone;
        private HouseholdMemberRole role;
        private Boolean isActive;
    }
}
