package com.apten.apartmentcomplex.application.mapper;

import com.apten.apartmentcomplex.application.model.dto.ApartmentComplexDto;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import org.springframework.stereotype.Component;

// 단지 엔티티를 내부 DTO로 변환하는 매퍼
// 조회 로직이 구체화되면 서비스에서 이 매퍼를 재사용할 수 있다
@Component
public class ApartmentComplexDtoMapper {

    // 단지 엔티티를 내부 DTO로 변환한다
    public ApartmentComplexDto toDto(ApartmentComplex apartmentComplex) {
        return ApartmentComplexDto.builder()
                .id(apartmentComplex.getId())
                .code(apartmentComplex.getCode())
                .name(apartmentComplex.getName())
                .address(apartmentComplex.getAddress())
                .zipCode(apartmentComplex.getZipCode())
                .status(apartmentComplex.getStatus())
                .description(apartmentComplex.getDescription())
                .build();
    }
}
