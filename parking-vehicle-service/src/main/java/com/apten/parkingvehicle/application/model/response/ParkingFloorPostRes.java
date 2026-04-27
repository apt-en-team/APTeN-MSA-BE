package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 주차층 등록 응답 DTO이다.
@Getter
@Builder
public class ParkingFloorPostRes {

    // 주차층 ID이다.
    private Long parkingFloorId;

    // 층 이름이다.
    private String floorName;

    // 전체 주차 면수이다.
    private Integer totalSlots;

    // 활성 여부이다.
    private Boolean isActive;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
