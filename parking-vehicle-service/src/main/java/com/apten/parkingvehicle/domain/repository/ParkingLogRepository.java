package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 주차 입출차 로그 원본 테이블 접근을 담당하는 저장소이다.
public interface ParkingLogRepository extends JpaRepository<ParkingLog, Long> {

    // 단지와 차량번호 기준 가장 최근 로그를 조회한다.
    Optional<ParkingLog> findTopByComplexIdAndLicensePlateOrderByLoggedAtDesc(Long complexId, String licensePlate);

    // 단지와 연월 기준 로그 목록을 조회한다.
    List<ParkingLog> findByComplexId(Long complexId);

    // 단지와 zone ID 목록 기준 현재 입차 중인 차량 수를 zone별로 집계한다.
    // NOT EXISTS 서브쿼리로 같은 차량번호의 더 최신 OUT 로그가 없는 IN 로그만 카운트한다.
    @Query("SELECT p.zoneId, COUNT(p) FROM ParkingLog p " +
            "WHERE p.complexId = :complexId " +
            "AND p.zoneId IN :zoneIds " +
            "AND p.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.IN " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM ParkingLog p2 " +
            "    WHERE p2.complexId = p.complexId " +
            "    AND p2.licensePlate = p.licensePlate " +
            "    AND p2.loggedAt > p.loggedAt" +
            ") " +
            "GROUP BY p.zoneId")
    List<Object[]> countCurrentParkedByZone(
            @Param("complexId") Long complexId,
            @Param("zoneIds") List<Long> zoneIds
    );
}
