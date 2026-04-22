package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 취소 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramCancelRes {
    private String gxProgramUid;
    private String status;
    private LocalDateTime cancelledAt;
    private String reason;
}
