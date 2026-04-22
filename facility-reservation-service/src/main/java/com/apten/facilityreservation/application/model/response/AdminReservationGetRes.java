package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 예약 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationGetRes {
    private Long reservationId;
    private String reservationUid;
    private String residentName;
    private String facilityName;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer seatNo;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
}
