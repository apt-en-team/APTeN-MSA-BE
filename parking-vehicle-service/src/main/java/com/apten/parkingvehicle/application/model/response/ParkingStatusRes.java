package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 현황 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatusRes {

    // 전체 주차 면수이다.
    private Integer totalSlots;

    // 현재 주차 대수이다.
    private Integer currentParkedCount;

    // 잔여 면수이다.
    private Integer remainingSlots;

    // 점유율이다.
    private BigDecimal occupancyRate;

    // 주차 운영 타입 code이다 (DB 저장값).
    private String parkingTypeCode;

    // 주차 운영 타입 value이다 (사용자 노출용).
    private String parkingTypeValue;

    // 현재 주차 중 입주민 차량 수이다. (SENSOR 단지는 null로 응답한다)
    private Integer residentCount;

    // 현재 주차 중 방문 차량 수이다. (SENSOR 단지는 null로 응답한다)
    private Integer visitorCount;

    // 현재 주차 중 고정 방문 차량 수이다. (SENSOR 단지는 null로 응답한다)
    private Integer regularVisitorCount;

    // 현재 주차 중 미등록 차량 수이다. (SENSOR 단지는 null로 응답한다)
    private Integer unregisteredCount;

    // 단지의 주차장(area) 개수이다. (활성 구역 기준 주차장명 중복 제거)
    private int areaCount;

    // 구역별 현재 점유 정보 목록이다.
    private List<ParkingZoneStatusRes> zones;

    // 갱신 시각이다.
    private LocalDateTime updatedAt;
}
