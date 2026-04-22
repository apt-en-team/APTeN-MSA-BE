package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 활성 상태 변경 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityActivePatchRes {
    private String facilityUid;
    private Boolean isActive;
    private LocalDateTime updatedAt;
}
