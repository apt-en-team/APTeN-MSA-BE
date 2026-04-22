package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPostRes {
    private Long facilityId;
    private String facilityUid;
    private String name;
    private LocalDateTime createdAt;
}
