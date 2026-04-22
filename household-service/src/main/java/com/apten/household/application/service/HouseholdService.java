package com.apten.household.application.service;

import com.apten.household.application.mapper.HouseholdDtoMapper;
import com.apten.household.application.model.dto.HouseholdDto;
import com.apten.household.application.model.response.HouseholdBaseResponse;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.enums.HouseholdStatus;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.infrastructure.mapper.HouseholdQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// household-service 유스케이스를 조합할 기본 서비스 클래스
// JPA와 MyBatis를 함께 사용하는 위치가 application/service라는 점을 구조로 보여준다
@Service
@RequiredArgsConstructor
public class HouseholdService {

    // 단건 저장과 기본 조회는 JPA Repository가 맡는다
    private final HouseholdRepository householdRepository;

    // 복잡 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<HouseholdQueryMapper> householdQueryMapperProvider;

    // 요청 DTO와 응답 DTO 변환은 전용 매퍼에 맡긴다
    private final HouseholdDtoMapper householdDtoMapper;

    // 컨트롤러가 바로 연결할 수 있는 최소 응답 형태를 반환한다
    public HouseholdBaseResponse getHouseholdTemplate() {
        Household household = Household.builder()
                .id(1L)
                .name("household-template")
                .status(HouseholdStatus.ACTIVE)
                .build();

        HouseholdDto householdDto = householdDtoMapper.toDto(household);
        return householdDtoMapper.toResponse(householdDto);
    }
}
