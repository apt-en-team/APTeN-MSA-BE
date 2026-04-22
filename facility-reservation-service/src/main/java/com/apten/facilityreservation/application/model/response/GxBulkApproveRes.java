package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 일괄 승인 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxBulkApproveRes {
    private String gxProgramUid;
    private Integer approvedCount;
    private Integer rejectedCount;
    private LocalDateTime processedAt;
}
