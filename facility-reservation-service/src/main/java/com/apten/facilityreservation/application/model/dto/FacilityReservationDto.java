package com.apten.facilityreservation.application.model.dto;

import com.apten.facilityreservation.domain.enums.FacilityReservationStatus;
import lombok.Builder;
import lombok.Getter;

// facility-reservation-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 직접 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class FacilityReservationDto {

    // 예약 식별자
    private final Long id;

    // 예약 이름
    private final String reservationName;

    // 예약 상태
    private final FacilityReservationStatus status;
}
