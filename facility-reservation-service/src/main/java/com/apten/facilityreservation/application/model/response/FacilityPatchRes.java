package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 수정 응답 DTO이다.
@Getter
@Builder
public class FacilityPatchRes {

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String name;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
