package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// household cache를 채우는 최소 필드만 담는 이벤트 payload
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdEventPayload {

    // 원본 세대 식별자
    private Long householdId;

    // 세대가 속한 단지 식별자
    private Long apartmentComplexId;

    // 동 정보
    private String buildingNo;

    // 호 정보
    private String unitNo;

    // 세대 상태값
    private String status;
}
