package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입주민 차량 등록 한도 정책 설정 요청 DTO이다.
// 세대당 등록 가능 최대 대수 정책을 받을 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRegistrationPolicyPutReq {

    // 세대당 등록 가능 최대 차량 대수이다.
    private Integer maxCarCount;

    // 정책 활성 여부이다.
    private Boolean isActive;
}
