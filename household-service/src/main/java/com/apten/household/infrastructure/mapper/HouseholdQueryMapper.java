package com.apten.household.infrastructure.mapper;

import com.apten.household.application.model.dto.HouseholdDto;
import java.util.List;

// 세대, 세대원, 상태 이력 조회를 분리할 MyBatis 매퍼
// 세대 도메인 서비스에서만 이 인터페이스를 사용한다
public interface HouseholdQueryMapper {

    // 세대 목록 조회가 필요해질 때 확장할 기본 메서드 위치
    List<HouseholdDto> findHouseholdSummaries();
}
