package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입주민 GX 프로그램 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentGxProgramListReq {

    // 조회 시작일 필터이다.
    private LocalDate fromDate;

    // 조회 종료일 필터이다.
    private LocalDate toDate;

    // 상태 필터이다.
    private GxProgramStatus status;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
