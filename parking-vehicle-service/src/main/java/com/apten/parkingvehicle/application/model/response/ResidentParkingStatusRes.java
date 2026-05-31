package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 입주민 주차 현황 조회 응답 DTO이다.
// 단지 전체 집계와 활성 구역별 잔여 면수를 함께 내려준다.
@Getter
@Builder
public class ResidentParkingStatusRes {

    // 주차 운영 타입 code이다 (DB 저장값).
    private String parkingTypeCode;

    // 주차 운영 타입 value이다 (사용자 노출용).
    private String parkingTypeValue;

    // 활성 구역 기준 전체 주차 면수이다.
    private Integer totalSlots;

    // 활성 구역 기준 현재 주차 대수이다.
    private Integer currentParkedCount;

    // 활성 구역 기준 잔여 면수이다.
    private Integer remainingSlots;

    // 활성 구역 기준 점유율이다.
    private BigDecimal occupancyRate;

    // 활성 구역별 잔여 면수 목록이다.
    private List<ParkingZoneStatusRes> zones;

    // 응답 생성 시각이다.
    private LocalDateTime updatedAt;
}