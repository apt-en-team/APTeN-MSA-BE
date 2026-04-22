package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 household cache 저장소
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {
}
