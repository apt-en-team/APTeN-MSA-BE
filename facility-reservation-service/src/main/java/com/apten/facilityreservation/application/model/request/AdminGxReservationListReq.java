package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 GX 프로그램 신청자 목록 요청 DTO이다.
@Getter
@NoArgsConstructor
public class AdminGxReservationListReq {

    // 상태 필터이다. null이면 전체 조회한다.
    private GxReservationStatus status;

    private int page = 0;

    private int size = 20;
}
