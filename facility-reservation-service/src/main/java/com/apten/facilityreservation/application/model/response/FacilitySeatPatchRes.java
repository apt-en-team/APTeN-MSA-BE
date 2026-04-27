package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 좌석 수정 응답 DTO이다.
@Getter
@Builder
public class FacilitySeatPatchRes {

    // 좌석 ID이다.
    private Long seatId;

    // 좌석명이다.
    private String seatName;

    // 정렬 순서이다.
    private Integer sortOrder;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
