package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityClosureRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 정기 휴무 규칙 저장소이다.
public interface FacilityClosureRuleRepository extends JpaRepository<FacilityClosureRule, Long> {

    // 시설의 활성 규칙 목록을 조회한다. 예약 가능 여부 체크와 관리자 목록 표시에 사용한다.
    List<FacilityClosureRule> findByFacilityIdAndIsActiveTrueOrderByCreatedAtDesc(Long facilityId);

    // 여러 시설의 활성 규칙을 일괄 조회한다. 시설 목록의 isTodayBlocked 배치 계산에 사용한다.
    List<FacilityClosureRule> findByFacilityIdInAndIsActiveTrue(List<Long> facilityIds);
}
