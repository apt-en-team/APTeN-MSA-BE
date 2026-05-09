package com.apten.household.domain.repository;

import com.apten.household.domain.entity.Household;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// household-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper의 MyBatis 매퍼로 분리하는 기준을 따른다
public interface HouseholdRepository extends JpaRepository<Household, Long> {

    // 단지와 동호수 조합 중복 여부를 확인한다.
    boolean existsByComplexIdAndBuildingAndUnit(Long complexId, String building, String unit);

    // 단지와 동호수 기준 세대를 조회한다.
    Optional<Household> findByComplexIdAndBuildingAndUnit(Long complexId, String building, String unit);
}
