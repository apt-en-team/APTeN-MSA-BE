package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdMatchRequest;
import com.apten.household.domain.enums.HouseholdMatchProcessType;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 세대 매칭 요청 저장소이다.
public interface HouseholdMatchRequestRepository extends JpaRepository<HouseholdMatchRequest, Long> {

    Page<HouseholdMatchRequest> findByMatchStatus(HouseholdMatchStatus matchStatus, Pageable pageable);

    boolean existsByUserIdAndComplexIdAndMatchStatusIn(
            Long userId,
            Long complexId,
            Collection<HouseholdMatchStatus> matchStatuses
    );

    @Query("""
            SELECT m FROM HouseholdMatchRequest m
            WHERE m.complexId = :complexId
              AND (:matchStatus IS NULL OR m.matchStatus = :matchStatus)
              AND (:processType IS NULL OR m.processType = :processType)
            """)
    Page<HouseholdMatchRequest> findByFilters(
            Long complexId,
            HouseholdMatchStatus matchStatus,
            HouseholdMatchProcessType processType,
            Pageable pageable
    );
}
