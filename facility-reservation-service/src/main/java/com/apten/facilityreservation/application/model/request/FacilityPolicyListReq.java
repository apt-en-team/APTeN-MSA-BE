package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 예약 정책 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyListReq {

    // 단지 ID이다.
    private Long complexId;

    // 시설 타입 코드 필터이다.
    private FacilityTypeCode facilityTypeCode;
}
