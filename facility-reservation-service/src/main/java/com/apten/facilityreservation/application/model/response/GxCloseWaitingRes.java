package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 모집 마감 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxCloseWaitingRes {

    // GX 프로그램 ID이다.
    private Long programId;

    // 변경 후 프로그램 상태이다.
    private GxProgramStatus status;

    // 거절 처리된 대기자 수이다.
    private int rejectedCount;

    // 처리 시각이다.
    private LocalDateTime processedAt;
}
