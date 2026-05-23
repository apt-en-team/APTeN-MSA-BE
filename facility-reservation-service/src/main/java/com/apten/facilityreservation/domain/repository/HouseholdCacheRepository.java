package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.HouseholdCache;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// facility-reservation-service의 household cache 저장소
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {

    // 세대 ID 기준 세대 캐시를 조회한다.
    Optional<HouseholdCache> findByHouseholdId(Long householdId);

    // 세대 ID와 단지 ID 기준 세대 캐시를 조회한다.
    Optional<HouseholdCache> findByHouseholdIdAndApartmentComplexId(Long householdId, Long apartmentComplexId);

    // 세대 ID와 단지 ID 일치 여부를 검증한다.
    boolean existsByHouseholdIdAndApartmentComplexId(Long householdId, Long apartmentComplexId);

    // 단지 내 전체 세대 목록을 조회한다.
    List<HouseholdCache> findByApartmentComplexId(Long apartmentComplexId);
}
