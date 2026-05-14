package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
