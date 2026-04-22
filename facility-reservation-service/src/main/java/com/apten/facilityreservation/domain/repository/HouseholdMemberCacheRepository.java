package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import org.springframework.data.jpa.repository.JpaRepository;

// facility-reservation-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {
}
