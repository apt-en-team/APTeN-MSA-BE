package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.HouseholdMemberCache;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {
}
