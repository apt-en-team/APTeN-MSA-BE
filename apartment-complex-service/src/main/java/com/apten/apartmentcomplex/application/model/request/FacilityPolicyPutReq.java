package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 정책 설정 요청 DTO
// 시설 예약 단위와 취소 정책 값을 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyPutReq {
    private Integer reservationSlotMin;
    private Integer facilityCancelDeadlineHours;
    private Boolean gxWaitingEnabled;
    private String memo;
}
