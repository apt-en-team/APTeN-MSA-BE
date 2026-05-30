package com.apten.parkingvehicle.application.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 차량 화면 동/호 옵션 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVehicleLocationRes {

    // 동별 호 목록이다.
    private List<Building> buildings;

    // 동 한 건과 그 동의 호 목록을 담는 항목이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Building {

        // 동 이름이다.
        private String building;

        // 해당 동의 호 목록이다.
        private List<String> units;
    }
}
