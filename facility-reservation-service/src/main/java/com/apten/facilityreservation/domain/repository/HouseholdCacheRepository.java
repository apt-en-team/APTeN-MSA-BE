package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.HouseholdCache;
import org.springframework.data.jpa.repository.JpaRepository;

// facility-reservation-service의 household cache 저장소
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {
}
