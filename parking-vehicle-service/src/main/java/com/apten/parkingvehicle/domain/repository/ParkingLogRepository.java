package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingLog;
import com.apten.parkingvehicle.domain.enums.ParkingEntryType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 동적 필터로 입출차 기록을 페이지 조회한다.
    // 필수 파라미터는 complexId, 나머지는 NULL이면 해당 조건을 건너뛴다.
    // 차종 분류는 ParkingLog에 별도 컬럼이 없으므로 FK 분기로 판별한다.
    // 정렬은 loggedAt DESC 고정 (Service에서 PageRequest는 unsorted로 전달할 것).
    @Query("""
            SELECT pl FROM ParkingLog pl
            WHERE pl.complexId = :complexId
              AND (:fromDateTime IS NULL OR pl.loggedAt >= :fromDateTime)
              AND (:toDateTime IS NULL OR pl.loggedAt < :toDateTime)
              AND (:entryType IS NULL OR pl.entryType = :entryType)
              AND (:licensePlate IS NULL OR pl.licensePlate LIKE CONCAT('%', :licensePlate, '%'))
              AND (
                :vehicleCategory IS NULL
                OR (:vehicleCategory = 'RESIDENT' AND pl.vehicleId IS NOT NULL)
                OR (:vehicleCategory = 'VISITOR' AND pl.visitorVehicleId IS NOT NULL)
                OR (:vehicleCategory = 'REGULAR_VISITOR' AND pl.regularVisitorVehicleId IS NOT NULL)
                OR (:vehicleCategory = 'UNREGISTERED'
                    AND pl.vehicleId IS NULL
                    AND pl.visitorVehicleId IS NULL
                    AND pl.regularVisitorVehicleId IS NULL)
              )
            ORDER BY pl.loggedAt DESC
            """)
    Page<ParkingLog> findFilteredLogs(
            @Param("complexId") Long complexId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("entryType") ParkingEntryType entryType,
            @Param("licensePlate") String licensePlate,
            @Param("vehicleCategory") String vehicleCategory,
            Pageable pageable
    );

    // 단지 + 기간 내의 모든 입출차 로그를 시간순으로 조회한다.
    // 시간대별/일별 집계를 메모리에서 처리하기 위해 한 번에 가져온다.
    // (전체 단지 일주일치 로그도 수천 건 수준이라 메모리 처리로 충분)
    @Query("""
            SELECT pl FROM ParkingLog pl
            WHERE pl.complexId = :complexId
              AND pl.loggedAt >= :fromDateTime
              AND pl.loggedAt < :toDateTime
            ORDER BY pl.loggedAt ASC
            """)
    List<ParkingLog> findLogsForStatistics(
            @Param("complexId") Long complexId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );

    // 특정 시점 기준 단지의 현재 입차 중인 차량 수를 단일 값으로 집계한다.
    // 시점별 점유율 계산에 사용한다.
    // 활성 zone만 카운트 (비활성 zone 입차 로그는 제외).
    @Query("""
            SELECT COUNT(p) FROM ParkingLog p
            WHERE p.complexId = :complexId
              AND p.loggedAt <= :asOfTime
              AND p.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.IN
              AND EXISTS (
                SELECT 1 FROM ParkingZone z
                WHERE z.id = p.zoneId
                  AND z.complexId = p.complexId
                  AND z.isActive = true
              )
              AND NOT EXISTS (
                SELECT 1 FROM ParkingLog p2
                WHERE p2.complexId = p.complexId
                  AND p2.licensePlate = p.licensePlate
                  AND p2.loggedAt > p.loggedAt
                  AND p2.loggedAt <= :asOfTime
              )
            """)
    int countParkedAt(
            @Param("complexId") Long complexId,
            @Param("asOfTime") LocalDateTime asOfTime
    );

    // 오늘과 어제의 입차/출차 건수를 한 쿼리에 집계
// 시안의 "오늘 입차 / 오늘 출차 / 전일 대비 차이" 계산용으로 사용
// 어제~내일 0시 범위 한 번만 스캔하고 SUM CASE로 4값 분리
    @Query("""
        SELECT
          SUM(CASE WHEN pl.loggedAt >= :todayStart
                   AND pl.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.IN
                   THEN 1L ELSE 0L END),
          SUM(CASE WHEN pl.loggedAt >= :todayStart
                   AND pl.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.OUT
                   THEN 1L ELSE 0L END),
          SUM(CASE WHEN pl.loggedAt < :todayStart
                   AND pl.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.IN
                   THEN 1L ELSE 0L END),
          SUM(CASE WHEN pl.loggedAt < :todayStart
                   AND pl.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.OUT
                   THEN 1L ELSE 0L END)
        FROM ParkingLog pl
        WHERE pl.complexId = :complexId
          AND pl.loggedAt >= :yesterdayStart
          AND pl.loggedAt < :tomorrowStart
        """)
    List<Object[]> sumTodayAndYesterdayCounts(
            @Param("complexId") Long complexId,
            @Param("yesterdayStart") LocalDateTime yesterdayStart,
            @Param("todayStart") LocalDateTime todayStart,
            @Param("tomorrowStart") LocalDateTime tomorrowStart
    );

    // 현재 단지 내 입차 중인 미등록 차량 수를 집계
    // 모든 FK(vehicleId, visitorVehicleId, regularVisitorVehicleId)가 NULL인 IN 로그 중
    // 같은 차량번호의 더 최신 로그(보통 OUT)가 없는 것만 카운트한다.
    @Query("""
            SELECT COUNT(p) FROM ParkingLog p
            WHERE p.complexId = :complexId
              AND p.entryType = com.apten.parkingvehicle.domain.enums.ParkingEntryType.IN
              AND p.vehicleId IS NULL
              AND p.visitorVehicleId IS NULL
              AND p.regularVisitorVehicleId IS NULL
              AND NOT EXISTS (
                SELECT 1 FROM ParkingLog p2
                WHERE p2.complexId = p.complexId
                  AND p2.licensePlate = p.licensePlate
                  AND p2.loggedAt > p.loggedAt
              )
            """)
    long countCurrentUnregistered(@Param("complexId") Long complexId);

    // 기간 내 전체 입출차 로그 건수 집계 (입차와 출차 모두 합산)
    @Query("""
            SELECT COUNT(p) FROM ParkingLog p
            WHERE p.complexId = :complexId
              AND p.loggedAt >= :fromDateTime
              AND p.loggedAt < :toDateTime
            """)
    long countLogsInRange(
            @Param("complexId") Long complexId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );
}