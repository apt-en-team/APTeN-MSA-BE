package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// household member cache를 채우는 최소 필드만 담는 이벤트 payload
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberEventPayload {

    // 원본 세대 구성원 식별자
    private Long householdMemberId;

    // 구성원이 속한 세대 식별자
    private Long householdId;

    // 연결된 사용자 식별자
    private Long userId;

    // 세대 내 역할
    private String memberRole;

    // 세대 구성원 상태값
    private String status;

    // 대표 구성원 여부
    private boolean isPrimary;
}
