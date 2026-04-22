package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.mapper.FacilityReservationDtoMapper;
import com.apten.facilityreservation.application.model.dto.FacilityReservationDto;
import com.apten.facilityreservation.application.model.response.FacilityReservationBaseResponse;
import com.apten.facilityreservation.domain.entity.FacilityReservation;
import com.apten.facilityreservation.domain.enums.FacilityReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityReservationRepository;
import com.apten.facilityreservation.infrastructure.mapper.FacilityReservationQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// facility-reservation-service 유스케이스를 조합할 기본 서비스 클래스
// JPA와 MyBatis를 함께 사용하는 위치가 application/service라는 점을 구조로 보여준다
@Service
@RequiredArgsConstructor
public class FacilityReservationService {

    // 단건 저장과 기본 조회는 JPA Repository가 맡는다
    private final FacilityReservationRepository facilityReservationRepository;

    // 복잡 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<FacilityReservationQueryMapper> facilityReservationQueryMapperProvider;

    // 요청 DTO와 응답 DTO 변환은 전용 매퍼에 맡긴다
    private final FacilityReservationDtoMapper facilityReservationDtoMapper;

    // 컨트롤러가 바로 연결할 수 있는 최소 응답 형태를 반환한다
    public FacilityReservationBaseResponse getFacilityReservationTemplate() {
        FacilityReservation facilityReservation = FacilityReservation.builder()
                .id(1L)
                .reservationName("facility-reservation-template")
                .status(FacilityReservationStatus.READY)
                .build();

        FacilityReservationDto facilityReservationDto = facilityReservationDtoMapper.toDto(facilityReservation);
        return facilityReservationDtoMapper.toResponse(facilityReservationDto);
    }
}
