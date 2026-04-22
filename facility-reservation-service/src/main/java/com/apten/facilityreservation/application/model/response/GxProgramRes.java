package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramRes {
    private Long gxProgramId;
    private String gxProgramUid;
    private String programName;
    private String facilityUid;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCount;
    private Integer confirmedCount;
    private Integer waitingCount;
    private Boolean isOpen;
}
