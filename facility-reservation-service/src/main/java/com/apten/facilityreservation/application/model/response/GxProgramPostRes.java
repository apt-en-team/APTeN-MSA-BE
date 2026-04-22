package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramPostRes {
    private Long gxProgramId;
    private String gxProgramUid;
    private String programName;
    private LocalDateTime createdAt;
}
