package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 일괄 승인 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxBulkApproveReq {
    private String gxProgramUid;
    private Integer approveCount;
}
