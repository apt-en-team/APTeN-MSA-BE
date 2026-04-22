package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramPostReq {
    private String facilityUid;
    private String programName;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCount;
    private Integer minCount;
    private Boolean isOpen;
}
