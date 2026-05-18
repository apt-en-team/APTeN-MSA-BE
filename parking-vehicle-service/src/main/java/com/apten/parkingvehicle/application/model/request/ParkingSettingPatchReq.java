package com.apten.parkingvehicle.application.model.request;

import com.apten.common.enums.ParkingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 주차 운영 타입 변경 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSettingPatchReq {

    // 변경할 주차 운영 타입이다.
    private ParkingType parkingType;
}