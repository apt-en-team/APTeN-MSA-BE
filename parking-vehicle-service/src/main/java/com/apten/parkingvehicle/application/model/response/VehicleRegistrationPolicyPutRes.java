package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 차량 등록 한도 정책 설정 응답 DTO이다.
// 저장된 등록 한도 정책 결과를 내려줄 때 사용한다.
@Getter
@Builder
public class VehicleRegistrationPolicyPutRes {

    // 단지 ID이다.
    private final Long complexId;

    // 세대당 등록 가능 최대 차량 대수이다.
    private final Integer maxCarCount;

    // 정책 활성 여부이다.
    private final Boolean isActive;

    // 정책 저장이 끝난 시각이다.
    private final LocalDateTime updatedAt;
}
