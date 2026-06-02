package com.apten.household.domain.repository;

import com.apten.household.domain.entity.Household;
import com.apten.household.domain.enums.HouseholdStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// household-service의 세대 저장과 단순 조회를 맡는 JPA Repository이다.
public interface HouseholdRepository extends JpaRepository<Household, Long> {

    // 단지와 동호수 조합 중복 여부를 확인한다.
    boolean existsByComplexIdAndBuildingAndUnit(Long complexId, String building, String unit);

    boolean existsByComplexIdAndBuildingAndUnitAndIdNot(Long complexId, String building, String unit, Long id);

    // 단지와 동호수 기준 세대를 조회한다.
    Optional<Household> findByComplexIdAndBuildingAndUnit(Long complexId, String building, String unit);

    // 세대 ID와 단지 ID 기준 세대를 조회한다.
    Optional<Household> findByIdAndComplexId(Long id, Long complexId);

    List<Household> findByComplexIdAndIdIn(Long complexId, List<Long> ids);

    List<Household> findByComplexIdAndBuilding(Long complexId, String building);

    List<Household> findByComplexIdAndStatus(Long complexId, HouseholdStatus status);
    @Query("""
            SELECT h FROM Household h
            WHERE h.complexId = :complexId
              AND (:building IS NULL OR h.building = :building)
              AND (:unit IS NULL OR h.unit = :unit)
            ORDER BY h.status ASC, h.building ASC, h.unit ASC
            """)
    Page<Household> findByFilters(
            @Param("complexId") Long complexId,
            @Param("building") String building,
            @Param("unit") String unit,
            Pageable pageable
    );

    @Query("""
            SELECT h FROM Household h
            WHERE h.complexId = :complexId
              AND (:building IS NULL OR h.building = :building)
              AND (:unit IS NULL OR h.unit = :unit)
              AND h.status = :status
            ORDER BY h.status ASC, h.building ASC, h.unit ASC
            """)
    Page<Household> findByFiltersAndStatus(
            @Param("complexId") Long complexId,
            @Param("building") String building,
            @Param("unit") String unit,
            @Param("status") HouseholdStatus status,
            Pageable pageable
    );

    long countByComplexId(Long complexId);

    long countByComplexIdAndStatus(Long complexId, HouseholdStatus status);

    long countByComplexIdAndCreatedAtBetween(Long complexId, LocalDateTime from, LocalDateTime to);

    long countByComplexIdAndStatusAndUpdatedAtBetween(Long complexId, HouseholdStatus status, LocalDateTime from, LocalDateTime to);
}
