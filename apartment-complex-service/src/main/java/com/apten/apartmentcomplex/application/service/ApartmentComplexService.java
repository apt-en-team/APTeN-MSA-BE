package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.mapper.ApartmentComplexDtoMapper;
import com.apten.apartmentcomplex.application.model.dto.ApartmentComplexDto;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexBaseResponse;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// apartment-complex-service 유스케이스를 조합할 기본 서비스 클래스
// JPA와 MyBatis를 함께 사용하는 위치가 application/service라는 점을 구조로 보여준다
@Service
@RequiredArgsConstructor
public class ApartmentComplexService {

    // 단건 저장과 기본 조회는 JPA Repository가 맡는다
    private final ApartmentComplexRepository apartmentComplexRepository;

    // 복잡 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<ApartmentComplexQueryMapper> apartmentComplexQueryMapperProvider;

    // 요청 DTO와 응답 DTO 변환은 전용 매퍼에 맡긴다
    private final ApartmentComplexDtoMapper apartmentComplexDtoMapper;

    // 컨트롤러가 바로 연결할 수 있는 최소 응답 형태를 반환한다
    public ApartmentComplexBaseResponse getApartmentComplexTemplate() {
        ApartmentComplex apartmentComplex = ApartmentComplex.builder()
                .id(1L)
                .name("apartment-complex-template")
                .status(ApartmentComplexStatus.ACTIVE)
                .build();

        ApartmentComplexDto apartmentComplexDto = apartmentComplexDtoMapper.toDto(apartmentComplex);
        return apartmentComplexDtoMapper.toResponse(apartmentComplexDto);
    }
}
