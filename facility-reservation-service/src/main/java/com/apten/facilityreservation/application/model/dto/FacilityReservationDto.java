package com.apten.facilityreservation.application.model.dto;

import lombok.Builder;
import lombok.Getter;

// facility-reservation-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 느슨하게 연결하는 중간 전달 객체다
@Getter
@Builder
public class FacilityReservationDto {
}
