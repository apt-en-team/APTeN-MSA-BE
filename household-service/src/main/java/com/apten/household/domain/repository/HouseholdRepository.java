package com.apten.household.domain.repository;

import com.apten.household.domain.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

// household-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper의 MyBatis 매퍼로 분리하는 기준을 따른다
public interface HouseholdRepository extends JpaRepository<Household, Long> {
}
