package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.ParkingType;
import lombok.Builder;
import lombok.Getter;

// 단지 주차 운영 타입 조회 응답 DTO이다.
@Getter
@Builder
public class ParkingSettingGetRes {

    // 단지 ID이다.
    private Long complexId;

    // 주차 운영 타입 code이다.
    private String parkingTypeCode;

    // 주차 운영 타입 표시 value이다.
    private String parkingTypeValue;

    // 정적 팩토리 — code/value를 동시에 채운다.
    public static ParkingSettingGetRes of(Long complexId, ParkingType parkingType) {
        return ParkingSettingGetRes.builder()
                .complexId(complexId)
                .parkingTypeCode(parkingType.getCode())
                .parkingTypeValue(parkingType.getValue())
                .build();
    }
}