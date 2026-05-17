package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

// 예약 가능 시간 조회 요청 DTO이다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeListReq {

    // 시설 ID이다.
    private Long facilityId;

    // 예약일이다. yyyy-MM-dd 형식으로 전달해야 한다.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate;
}
