package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// facility policy cache를 채우는 최소 필드만 담는 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyEventPayload {

    // 정책이 속한 원본 단지 식별자이다.
    private Long apartmentComplexId;

    // 예약 단위 분이다.
    private Integer reservationSlotMin;

    // 예약 취소 마감 시간이다.
    private Integer facilityCancelDeadlineHours;

    // GX 대기 기능 사용 여부이다.
    private Boolean gxWaitingEnabled;

    // 현재 정책 활성 여부이다.
    private Boolean isActive;
}
