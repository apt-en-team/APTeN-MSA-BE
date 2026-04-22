package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 임시선점 생성 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSeatHoldPostRes {
    private String holdToken;
    private String facilityUid;
    private Integer seatNo;
    private LocalDateTime expiresAt;
    private String status;
}
