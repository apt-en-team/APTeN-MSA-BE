package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdMatchRequest;
import com.apten.household.domain.enums.HouseholdMatchProcessType;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 세대 매칭 요청 저장소이다.
public interface HouseholdMatchRequestRepository extends JpaRepository<HouseholdMatchRequest, Long> {

    Page<HouseholdMatchRequest> findByMatchStatus(HouseholdMatchStatus matchStatus, Pageable pageable);

    boolean existsByUserIdAndComplexIdAndMatchStatusIn(
            Long userId,
            Long complexId,
            Collection<HouseholdMatchStatus> matchStatuses
    );

    Page<HouseholdMatchRequest> findByComplexId(Long complexId, Pageable pageable);

    Page<HouseholdMatchRequest> findByComplexIdAndMatchStatus(
            Long complexId,
            HouseholdMatchStatus matchStatus,
            Pageable pageable
    );

    Page<HouseholdMatchRequest> findByComplexIdAndProcessType(
            Long complexId,
            HouseholdMatchProcessType processType,
            Pageable pageable
    );

    Page<HouseholdMatchRequest> findByComplexIdAndMatchStatusAndProcessType(
            Long complexId,
            HouseholdMatchStatus matchStatus,
            HouseholdMatchProcessType processType,
            Pageable pageable
    );

    Optional<HouseholdMatchRequest> findTopByComplexIdAndInputNameAndInputPhoneAndInputBirthDateAndInputBuildingAndInputUnitOrderByCreatedAtDesc(
            Long complexId,
            String inputName,
            String inputPhone,
            LocalDate inputBirthDate,
            String inputBuilding,
            String inputUnit
    );
}
