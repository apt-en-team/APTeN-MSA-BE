package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 수정 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPatchRes {
    private String facilityUid;
    private String name;
    private LocalDateTime updatedAt;
}
