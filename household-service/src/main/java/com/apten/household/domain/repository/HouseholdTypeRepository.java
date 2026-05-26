package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdTypeRepository extends JpaRepository<HouseholdType, Long> {

    Optional<HouseholdType> findByTypeCode(String typeCode);

    List<HouseholdType> findByIsActiveTrue();

    boolean existsByTypeCode(String typeCode);
}
