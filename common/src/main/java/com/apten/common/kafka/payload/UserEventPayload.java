package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// user cache를 채우는 최소 필드만 담는 이벤트 payload
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventPayload {

    // 원본 사용자 식별자
    private Long userId;

    // 소비 서비스 화면에서 표시할 이름
    private String name;

    // 현재 사용자 역할
    private String role;

    // 활성 여부를 포함한 상태값
    private String status;

    // 사용자 소속 단지 식별자
    private Long apartmentComplexId;
}
