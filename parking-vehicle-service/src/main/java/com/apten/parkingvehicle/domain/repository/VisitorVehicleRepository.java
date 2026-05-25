package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.VisitorVehicle;
import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 방문차량 원본 테이블 접근을 담당하는 저장소이다.
public interface VisitorVehicleRepository extends JpaRepository<VisitorVehicle, Long> {

    // 방문차량 식별자와 미삭제 조건으로 방문차량을 조회한다.
    Optional<VisitorVehicle> findByIdAndIsDeletedFalse(Long id);

    // 만료 대상 방문차량을 조회한다.
    List<VisitorVehicle> findByVisitDateBeforeAndStatusAndIsDeletedFalse(LocalDate visitDate, VisitorVehicleStatus status);

    // 단지와 차량번호, 방문 예정일 기준 활성 방문차량 중 가장 최근 1건을 조회한다 (입차 시 매칭용).
    Optional<VisitorVehicle> findTopByComplexIdAndLicensePlateAndVisitDateAndStatusAndIsDeletedFalseOrderByIdDesc(
            Long complexId, String licensePlate, LocalDate visitDate, VisitorVehicleStatus status);

    // 방문차량 단건 + 소유자 동시 조회, 미삭제 조건 포함
    Optional<VisitorVehicle> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    // 내 방문차량 목록 페이지 조회, 기간 동적 필터
    @Query("""
            SELECT v FROM VisitorVehicle v
            WHERE v.userId = :userId
              AND v.complexId = :complexId
              AND v.isDeleted = false
              AND (:fromDate IS NULL OR v.visitDate >= :fromDate)
              AND (:toDate IS NULL OR v.visitDate <= :toDate)
            ORDER BY v.createdAt DESC
            """)
    Page<VisitorVehicle> findMyVisitorVehicles(
            @Param("userId") Long userId,
            @Param("complexId") Long complexId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 내 방문차량 목록 페이지 조회, 상태와 기간 동적 필터
    @Query("""
            SELECT v FROM VisitorVehicle v
            WHERE v.userId = :userId
              AND v.complexId = :complexId
              AND v.isDeleted = false
              AND v.status = :status
              AND (:fromDate IS NULL OR v.visitDate >= :fromDate)
              AND (:toDate IS NULL OR v.visitDate <= :toDate)
            ORDER BY v.createdAt DESC
            """)
    Page<VisitorVehicle> findMyVisitorVehiclesByStatus(
            @Param("userId") Long userId,
            @Param("complexId") Long complexId,
            @Param("status") VisitorVehicleStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 관리자 방문 예정 차량 목록 페이지 조회, 기간과 키워드 동적 필터
    @Query("""
            SELECT v FROM VisitorVehicle v
            LEFT JOIN HouseholdCache h ON h.id = v.householdId
            LEFT JOIN UserCache u ON u.id = v.userId
            WHERE v.complexId = :complexId
              AND v.isDeleted = false
              AND (:fromDate IS NULL OR v.visitDate >= :fromDate)
              AND (:toDate IS NULL OR v.visitDate <= :toDate)
              AND (:keyword IS NULL OR :keyword = ''
                   OR v.licensePlate LIKE CONCAT('%', :keyword, '%')
                   OR v.visitorName LIKE CONCAT('%', :keyword, '%')
                   OR u.name LIKE CONCAT('%', :keyword, '%'))
            ORDER BY v.createdAt DESC
            """)
    Page<VisitorVehicle> findAdminVisitorVehicles(
            @Param("complexId") Long complexId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
