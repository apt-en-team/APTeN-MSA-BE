package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityClosureRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 정기 휴무 규칙 저장/조회 Repository
public interface FacilityClosureRuleRepository extends JpaRepository<FacilityClosureRule, Long> {

    // 활성 휴무 규칙 조회
    List<FacilityClosureRule> findByFacilityIdAndIsActiveTrueOrderByCreatedAtDesc(Long facilityId);

    // 복수 시설 활성 휴무 규칙 조회 (N+1 방지)
    List<FacilityClosureRule> findByFacilityIdInAndIsActiveTrue(List<Long> facilityIds);
}
