package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 GX 프로그램 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramListReq {

    // 단지 ID이다.
    private Long complexId;

    // 상태 필터이다.
    private GxProgramStatus status;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
