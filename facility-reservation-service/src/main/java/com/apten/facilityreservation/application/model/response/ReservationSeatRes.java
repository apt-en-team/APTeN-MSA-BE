package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 상태 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSeatRes {
    private Integer seatNo;
    private String status;
    private LocalDateTime holdExpireAt;
}
