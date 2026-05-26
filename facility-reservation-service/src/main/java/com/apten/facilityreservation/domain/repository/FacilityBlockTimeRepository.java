package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 시설 차단 시간 저장소이다.
public interface FacilityBlockTimeRepository extends JpaRepository<FacilityBlockTime, Long> {

    // 시설 기준 차단 시간 목록을 조회한다.
    List<FacilityBlockTime> findByFacilityId(Long facilityId);

    // 특정 날짜의 활성 차단 시간 목록을 조회한다.
    List<FacilityBlockTime> findByFacilityIdAndBlockDateAndIsActiveTrue(Long facilityId, LocalDate blockDate);

    // 시설 차단 시간 목록 조회
    @Query("""
            SELECT b
            FROM FacilityBlockTime b
            WHERE b.facilityId = :facilityId
              AND (:fromDate IS NULL OR b.blockDate >= :fromDate)
              AND (:toDate IS NULL OR b.blockDate <= :toDate)
              AND (:isActive IS NULL OR b.isActive = :isActive)
            ORDER BY b.blockDate DESC, b.startTime ASC
            """)
    List<FacilityBlockTime> findBlockTimes(
            @Param("facilityId") Long facilityId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("isActive") Boolean isActive
    );

    // batchId 기준 일괄 비활성화 (facilityId 포함해 타 시설 접근 차단)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE FacilityBlockTime b SET b.isActive = false WHERE b.batchId = :batchId AND b.facilityId = :facilityId")
    int deactivateByBatchIdAndFacilityId(@Param("batchId") Long batchId, @Param("facilityId") Long facilityId);

    // 시설별 특정 날짜 활성 차단 존재 여부를 배치 조회한다. (N+1 방지용, 점검중 표시에 사용)
    @Query("""
        SELECT b.facilityId
        FROM FacilityBlockTime b
        WHERE b.facilityId IN :facilityIds
          AND b.blockDate = :date
          AND b.isActive = true
        GROUP BY b.facilityId
        """)
    List<Long> findBlockedFacilityIds(
            @Param("facilityIds") List<Long> facilityIds,
            @Param("date") LocalDate date
    );

}
