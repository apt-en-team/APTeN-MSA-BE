package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ApartmentComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 apartment complex cache 저장소
public interface ApartmentComplexCacheRepository extends JpaRepository<ApartmentComplexCache, Long> {
}
