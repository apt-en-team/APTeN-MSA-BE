package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 예약 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReservationSearchReq {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String status;
    private Integer page;
    private Integer size;

    public int getPage() {
        return page != null ? page : 0;
    }

    public int getSize() {
        return size != null ? size : 20;
    }
}
