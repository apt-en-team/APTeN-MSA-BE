package com.apten.household.application.mapper;

import com.apten.household.application.model.dto.HouseholdDto;
import com.apten.household.application.model.request.HouseholdBaseRequest;
import com.apten.household.application.model.response.HouseholdBaseResponse;
import com.apten.household.domain.entity.Household;
import org.springframework.stereotype.Component;

// household-service의 요청, 응답, 내부 DTO 변환을 맡는 전용 매퍼
// 서비스가 변환 코드까지 떠안지 않도록 application 계층 안에서 역할을 분리한다
@Component
public class HouseholdDtoMapper {

    // 요청 DTO를 저장 전 엔티티 형태로 옮긴다
    public Household toEntity(HouseholdBaseRequest request) {
        return Household.builder()
                .building(request.getBuilding())
                .unit(request.getUnit())
                .status(request.getStatus())
                .build();
    }

    // 엔티티를 서비스 내부 전달용 DTO로 바꾼다
    public HouseholdDto toDto(Household household) {
        return HouseholdDto.builder()
                .id(household.getId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .status(household.getStatus())
                .build();
    }

    // 내부 DTO를 외부 응답 모델로 바꾼다
    public HouseholdBaseResponse toResponse(HouseholdDto householdDto) {
        return HouseholdBaseResponse.builder()
                .id(householdDto.getId())
                .building(householdDto.getBuilding())
                .unit(householdDto.getUnit())
                .status(householdDto.getStatus())
                .build();
    }
}
