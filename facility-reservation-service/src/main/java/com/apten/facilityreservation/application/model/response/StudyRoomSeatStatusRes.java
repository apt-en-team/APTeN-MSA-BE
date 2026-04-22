package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 독서실 좌석 현황 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoomSeatStatusRes {
    private LocalDate targetDate;
    private LocalTime startTime;
    private List<StudyRoomSeatRes> seats;
}
