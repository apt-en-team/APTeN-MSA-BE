package com.apten.parkingvehicle.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 입출차 기록 조회 시 차량을 분류하는 enum이다.
// ParkingLog의 vehicle_id, visitor_vehicle_id, regular_visitor_vehicle_id 중
// 어느 FK가 채워져 있는지에 따라 분류가 결정된다.
@Getter
@RequiredArgsConstructor
public enum LogVehicleCategory {

    // 입주민 등록 차량 (vehicle_id 매칭됨)
    RESIDENT("입주민"),

    // 방문 차량 (visitor_vehicle_id 매칭됨)
    VISITOR("방문"),

    // 고정 방문 차량 (regular_visitor_vehicle_id 매칭됨)
    REGULAR_VISITOR("고정 방문"),

    // 미등록 차량 (어느 FK도 매칭되지 않은 번호판)
    UNREGISTERED("미등록");

    private final String label;

    // 프론트에서 name(RESIDENT)과 label(입주민) 둘 다 역직렬화되게 처리한다.
    // SocialProvider 패턴과 동일.
    @JsonCreator
    public static LogVehicleCategory from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (LogVehicleCategory category : values()) {
            if (category.name().equalsIgnoreCase(value) || category.label.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("알 수 없는 차종 분류: " + value);
    }
}