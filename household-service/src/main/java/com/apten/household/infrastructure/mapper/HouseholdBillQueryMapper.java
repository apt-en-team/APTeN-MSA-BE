package com.apten.household.infrastructure.mapper;

import com.apten.household.application.model.response.HouseholdBillRes;
import java.util.List;

// 세대 비용 조회와 정산 조회를 분리할 MyBatis 매퍼
// 세대 비용 도메인 서비스에서만 이 인터페이스를 사용한다
public interface HouseholdBillQueryMapper {

    // 입주민 세대 비용 조회가 필요해질 때 확장할 기본 메서드 위치
    List<HouseholdBillRes> findHouseholdBills();
}
