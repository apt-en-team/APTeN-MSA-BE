package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 임시선점 해제 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSeatHoldDeleteRes {
    private String message;
    private LocalDateTime releasedAt;
}
