package com.apten.parkingvehicle.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 주차 센서 일괄 등록 응답 DTO이다.
@Getter
@Builder
public class ParkingSensorBulkPostRes {

    // 생성된 센서 개수이다.
    private int createdCount;

    // 생성된 센서 ID 목록이다.
    private List<Long> createdIds;
}
