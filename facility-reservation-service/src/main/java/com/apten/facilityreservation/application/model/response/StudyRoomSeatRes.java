package com.apten.facilityreservation.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 독서실 좌석 단건 상태 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoomSeatRes {
    private Integer seatNo;
    private String status;
    private String residentName;
}
