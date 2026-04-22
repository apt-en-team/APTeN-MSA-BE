package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 GX 예약 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyGxReservationGetRes {
    private Long gxReservationId;
    private String gxReservationUid;
    private String gxProgramUid;
    private String programName;
    private LocalDate programDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Integer waitingNumber;
    private LocalDateTime createdAt;
}
