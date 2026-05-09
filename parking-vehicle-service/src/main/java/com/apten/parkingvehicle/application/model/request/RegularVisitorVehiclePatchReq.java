package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 고정 방문차량 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularVisitorVehiclePatchReq {

    // 방문자 이름이다.
    private String visitorName;

    // 연락처이다.
    private String phone;

    // 시작일이다.
    private LocalDate startDate;

    // 종료일이다.
    private LocalDate endDate;

    // 활성 여부이다.
    private Boolean isActive;
}
