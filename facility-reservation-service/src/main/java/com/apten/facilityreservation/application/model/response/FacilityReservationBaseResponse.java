package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// facility-reservation-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 예약 전용 응답 객체를 이 패키지에서 관리한다
@Getter
@Builder
public class FacilityReservationBaseResponse {
}
