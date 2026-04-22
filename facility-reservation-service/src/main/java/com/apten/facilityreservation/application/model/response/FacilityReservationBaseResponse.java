package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityReservationStatus;
import lombok.Builder;
import lombok.Getter;

// facility-reservation-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 예약 전용 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class FacilityReservationBaseResponse {

    // 예약 식별자
    private final Long id;

    // 예약 이름
    private final String reservationName;

    // 예약 상태
    private final FacilityReservationStatus status;
}
