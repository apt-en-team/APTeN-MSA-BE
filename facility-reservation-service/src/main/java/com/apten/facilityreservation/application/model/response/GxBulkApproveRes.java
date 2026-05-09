package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 일괄 승인 응답 DTO이다.
@Getter
@Builder
public class GxBulkApproveRes {

    // 프로그램 ID이다.
    private Long programId;

    // 승인 건수이다.
    private Integer approvedCount;

    // 처리 시각이다.
    private LocalDateTime processedAt;
}
