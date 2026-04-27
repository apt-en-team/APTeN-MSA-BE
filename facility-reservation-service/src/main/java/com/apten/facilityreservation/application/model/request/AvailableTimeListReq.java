package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 가능 시간 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeListReq {

    // 시설 ID이다.
    private Long facilityId;

    // 예약일이다.
    private LocalDate reservationDate;
}
