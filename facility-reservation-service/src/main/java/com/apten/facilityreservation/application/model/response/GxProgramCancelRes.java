package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 프로그램 취소 응답 DTO이다.
@Getter
@Builder
public class GxProgramCancelRes {

    // 프로그램 ID이다.
    private Long programId;

    // 상태이다.
    private GxProgramStatus status;

    // 취소 시각이다.
    private LocalDateTime cancelledAt;
}
