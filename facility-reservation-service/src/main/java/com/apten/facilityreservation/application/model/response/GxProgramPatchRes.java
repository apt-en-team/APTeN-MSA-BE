package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 프로그램 수정 응답 DTO이다.
@Getter
@Builder
public class GxProgramPatchRes {

    // 프로그램 ID이다.
    private Long programId;

    // 프로그램명이다.
    private String name;

    // 상태이다.
    private GxProgramStatus status;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
