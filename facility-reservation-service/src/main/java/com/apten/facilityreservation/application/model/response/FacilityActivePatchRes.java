package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 활성 상태 변경 응답 DTO이다.
@Getter
@Builder
public class FacilityActivePatchRes {

    // 시설 ID이다.
    private Long facilityId;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
