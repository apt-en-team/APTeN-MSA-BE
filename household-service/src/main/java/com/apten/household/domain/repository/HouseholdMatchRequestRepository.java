package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdMatchRequest;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대 매칭 요청 저장소이다.
public interface HouseholdMatchRequestRepository extends JpaRepository<HouseholdMatchRequest, Long> {

    // 상태 기준 매칭 요청 목록을 페이지 단위로 조회한다.
    Page<HouseholdMatchRequest> findByMatchStatus(HouseholdMatchStatus matchStatus, Pageable pageable);
}
