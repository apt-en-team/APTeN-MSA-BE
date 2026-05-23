package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.SensorStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Mock 시연용 센서 일괄 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorMockBulkPostReq {

    // 소속 주차 구역 ID이다. (일괄 등록은 단일 zone 한정)
    private Long zoneId;

    // 소속 단지 ID이다.
    private Long complexId;

    // 일괄 등록할 센서 항목 목록이다.
    private List<Item> items;

    // 일괄 등록 항목 단위 데이터이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 센서 식별 코드이다.
        private String sensorCode;

        // 초기 점유 상태이다.
        private SensorStatus initialStatus;
    }
}
