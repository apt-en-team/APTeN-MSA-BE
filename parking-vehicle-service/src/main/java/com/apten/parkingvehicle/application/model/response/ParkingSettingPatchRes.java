package com.apten.parkingvehicle.application.model.response;

import com.apten.common.enums.ParkingType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 단지 주차 운영 타입 변경 응답 DTO이다.
@Getter
@Builder
public class ParkingSettingPatchRes {

    // 단지 ID이다.
    private Long complexId;

    // 변경된 주차 운영 타입 code이다.
    private String parkingTypeCode;

    // 변경된 주차 운영 타입 표시 value이다.
    private String parkingTypeValue;

    // 수정 시각이다.
    private LocalDateTime updatedAt;

    // 정적 팩토리
    public static ParkingSettingPatchRes of(Long complexId, ParkingType parkingType, LocalDateTime updatedAt) {
        return ParkingSettingPatchRes.builder()
                .complexId(complexId)
                .parkingTypeCode(parkingType.getCode())
                .parkingTypeValue(parkingType.getValue())
                .updatedAt(updatedAt)
                .build();
    }
}