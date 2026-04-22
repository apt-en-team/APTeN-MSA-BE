package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.ApartmentComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// facility-reservation-service의 apartment complex cache 저장소
public interface ApartmentComplexCacheRepository extends JpaRepository<ApartmentComplexCache, Long> {
}
