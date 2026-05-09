package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 차량번호 중복 확인 응답 DTO이다.
@Getter
@Builder
public class LicensePlateCheckRes {

    // 중복 여부이다.
    private Boolean isDuplicate;
}
