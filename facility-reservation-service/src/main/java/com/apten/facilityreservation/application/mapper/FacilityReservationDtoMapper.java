package com.apten.facilityreservation.application.mapper;

import com.apten.facilityreservation.application.model.dto.FacilityReservationDto;
import com.apten.facilityreservation.application.model.request.FacilityReservationBaseRequest;
import com.apten.facilityreservation.application.model.response.FacilityReservationBaseResponse;
import com.apten.facilityreservation.domain.entity.FacilityReservation;
import org.springframework.stereotype.Component;

// facility-reservation-service의 요청, 응답, 내부 DTO 변환을 맡는 전용 매퍼
// 서비스가 변환 코드까지 떠안지 않도록 application 계층 안에서 역할을 분리한다
@Component
public class FacilityReservationDtoMapper {

    // 요청 DTO를 저장 전 엔티티 형태로 옮긴다
    public FacilityReservation toEntity(FacilityReservationBaseRequest request) {
        return FacilityReservation.builder()
                .reservationName(request.getReservationName())
                .status(request.getStatus())
                .build();
    }

    // 엔티티를 서비스 내부 전달용 DTO로 바꾼다
    public FacilityReservationDto toDto(FacilityReservation facilityReservation) {
        return FacilityReservationDto.builder()
                .id(facilityReservation.getId())
                .reservationName(facilityReservation.getReservationName())
                .status(facilityReservation.getStatus())
                .build();
    }

    // 내부 DTO를 외부 응답 모델로 바꾼다
    public FacilityReservationBaseResponse toResponse(FacilityReservationDto facilityReservationDto) {
        return FacilityReservationBaseResponse.builder()
                .id(facilityReservationDto.getId())
                .reservationName(facilityReservationDto.getReservationName())
                .status(facilityReservationDto.getStatus())
                .build();
    }
}
