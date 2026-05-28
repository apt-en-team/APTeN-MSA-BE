package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 차량 등록 한도 정책 조회 응답 DTO이다.
@Getter
@Builder
public class VehicleRegistrationPolicyGetRes {

    // 단지 ID이다.
    private Long complexId;

    // 세대당 등록 가능 최대 차량 대수이다.
    private Integer maxCarCount;

    // 정책 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
