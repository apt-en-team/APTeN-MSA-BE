package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// apartment complex cache를 채우는 최소 필드만 담는 이벤트 payload
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexEventPayload {

    // 원본 단지 식별자
    private Long apartmentComplexId;

    // 소비 서비스에서 표시할 단지명
    private String name;

    // 단지 주소
    private String address;

    // 단지 사용 상태
    private String status;
}
