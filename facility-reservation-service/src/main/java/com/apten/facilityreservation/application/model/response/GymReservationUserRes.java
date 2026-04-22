package com.apten.facilityreservation.application.model.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 헬스장 이용자 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymReservationUserRes {
    private String reservationUid;
    private String residentName;
    private LocalTime startTime;
    private LocalTime endTime;
}
