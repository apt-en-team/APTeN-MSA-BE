package com.apten.apartmentcomplex.application.mapper;

import com.apten.apartmentcomplex.application.model.dto.ApartmentComplexDto;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexBaseRequest;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexBaseResponse;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import org.springframework.stereotype.Component;

// apartment-complex-service의 요청, 응답, 내부 DTO 변환을 맡는 전용 매퍼
// 서비스가 변환 코드까지 떠안지 않도록 application 계층 안에서 역할을 분리한다
@Component
public class ApartmentComplexDtoMapper {

    // 요청 DTO를 저장 전 엔티티 형태로 옮긴다
    public ApartmentComplex toEntity(ApartmentComplexBaseRequest request) {
        return ApartmentComplex.builder()
                .name(request.getName())
                .status(request.getStatus())
                .build();
    }

    // 엔티티를 서비스 내부 전달용 DTO로 바꾼다
    public ApartmentComplexDto toDto(ApartmentComplex apartmentComplex) {
        return ApartmentComplexDto.builder()
                .id(apartmentComplex.getId())
                .name(apartmentComplex.getName())
                .status(apartmentComplex.getStatus())
                .build();
    }

    // 내부 DTO를 외부 응답 모델로 바꾼다
    public ApartmentComplexBaseResponse toResponse(ApartmentComplexDto apartmentComplexDto) {
        return ApartmentComplexBaseResponse.builder()
                .id(apartmentComplexDto.getId())
                .name(apartmentComplexDto.getName())
                .status(apartmentComplexDto.getStatus())
                .build();
    }
}
