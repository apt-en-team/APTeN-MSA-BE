package com.apten.household.infrastructure.mapper;

import com.apten.household.application.model.response.HouseholdMatchGetRes;
import java.util.List;

// 세대 매칭 요청과 승인 목록 조회를 분리할 MyBatis 매퍼
// 세대 매칭 도메인 서비스에서만 이 인터페이스를 사용한다
public interface HouseholdMatchQueryMapper {

    // 매칭 요청 목록 조회가 필요해질 때 확장할 기본 메서드 위치
    List<HouseholdMatchGetRes> findMatchRequestSummaries();
}
