package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.FacilityReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// facility-reservation-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 시설 예약 등록과 수정에서 공통으로 받을 값만 먼저 정리해 둔다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReservationBaseRequest {

    // 예약 이름 입력값
    private String reservationName;

    // 예약 상태 입력값
    private FacilityReservationStatus status;
}
