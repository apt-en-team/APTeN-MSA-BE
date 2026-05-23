package com.apten.household.domain.repository;

import com.apten.household.domain.entity.BuildingLineType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BuildingLineTypeRepository extends JpaRepository<BuildingLineType, Long> {

    List<BuildingLineType> findByComplexIdAndBuildingAndIsActiveTrue(Long complexId, String building);

    Optional<BuildingLineType> findByIdAndComplexId(Long id, Long complexId);

    boolean existsByTypeIdAndIsActiveTrue(Long typeId);

    @Query("SELECT b FROM BuildingLineType b WHERE b.complexId = :complexId" +
            " AND (:building IS NULL OR b.building = :building)" +
            " AND b.isActive = true ORDER BY b.building ASC, b.lineStart ASC")
    List<BuildingLineType> findActiveByFilters(
            @Param("complexId") Long complexId,
            @Param("building") String building
    );

    @Query("SELECT COUNT(b) > 0 FROM BuildingLineType b WHERE b.complexId = :complexId" +
            " AND b.building = :building" +
            " AND b.isActive = true" +
            " AND (:excludeId IS NULL OR b.id <> :excludeId)" +
            " AND b.lineStart <= :lineEnd" +
            " AND b.lineEnd >= :lineStart")
    boolean existsActiveOverlap(
            @Param("complexId") Long complexId,
            @Param("building") String building,
            @Param("lineStart") Integer lineStart,
            @Param("lineEnd") Integer lineEnd,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT b FROM BuildingLineType b WHERE b.complexId = :complexId" +
            " AND b.building = :building" +
            " AND b.isActive = true" +
            " AND b.lineStart <= :lineNumber" +
            " AND b.lineEnd >= :lineNumber")
    Optional<BuildingLineType> findActiveByUnitLine(
            @Param("complexId") Long complexId,
            @Param("building") String building,
            @Param("lineNumber") Integer lineNumber
    );
}
