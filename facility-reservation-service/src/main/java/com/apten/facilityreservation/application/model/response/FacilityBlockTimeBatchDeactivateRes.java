package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 반복 차단 그룹 비활성화 응답 DTO이다.
@Getter
@Builder
public class FacilityBlockTimeBatchDeactivateRes {

    private Long batchId;

    private int deactivatedCount;

    private LocalDateTime processedAt;
}
