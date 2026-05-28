package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 입주민 차량 원본 테이블 접근을 담당하는 저장소이다.
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 단지 기준 차량번호 중복 여부를 조회한다 (소프트 삭제 이력 포함).
    boolean existsByComplexIdAndLicensePlate(Long complexId, String licensePlate);

    // 세대 기준 승인 차량 수를 조회한다.
    long countByHouseholdIdAndStatusAndIsDeletedFalse(Long householdId, VehicleStatus status);

    // 세대 기준 여러 상태의 차량 수를 한 번에 조회한다.
    long countByHouseholdIdAndStatusInAndIsDeletedFalse(Long householdId, Collection<VehicleStatus> statuses);

    // 차량 식별자와 미삭제 조건으로 차량을 조회한다.
    Optional<Vehicle> findByIdAndIsDeletedFalse(Long id);

    // 단지와 차량번호 기준 승인된 미삭제 차량을 조회한다 (입차 시 입주민 차량 매칭용).
    Optional<Vehicle> findByComplexIdAndLicensePlateAndStatusAndIsDeletedFalse(
            Long complexId, String licensePlate, VehicleStatus status);

    // 내 차량 목록 페이지 조회
    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.userId = :userId
              AND v.complexId = :complexId
              AND v.isDeleted = false
            ORDER BY v.createdAt DESC
            """)
    Page<Vehicle> findMyVehicles(
            @Param("userId") Long userId,
            @Param("complexId") Long complexId,
            Pageable pageable
    );

    // 내 차량 목록 페이지 조회, 상태 필터 포함
    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.userId = :userId
              AND v.complexId = :complexId
              AND v.isDeleted = false
              AND v.status = :status
            ORDER BY v.createdAt DESC
            """)
    Page<Vehicle> findMyVehiclesByStatus(
            @Param("userId") Long userId,
            @Param("complexId") Long complexId,
            @Param("status") VehicleStatus status,
            Pageable pageable
    );

    // 내 차량 단건 조회, 소유자 검증 겸용
    Optional<Vehicle> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    // 관리자 차량 목록 페이지 조회, 동/호/키워드 동적 필터
    @Query("""
            SELECT v FROM Vehicle v
            LEFT JOIN HouseholdCache h ON h.id = v.householdId
            LEFT JOIN UserCache u ON u.id = v.userId
            WHERE v.complexId = :complexId
              AND v.isDeleted = false
              AND (:building IS NULL OR :building = '' OR h.building = :building)
              AND (:unit IS NULL OR :unit = '' OR h.unit = :unit)
              AND (:keyword IS NULL OR :keyword = ''
                   OR v.licensePlate LIKE CONCAT('%', :keyword, '%')
                   OR v.modelName LIKE CONCAT('%', :keyword, '%')
                   OR u.name LIKE CONCAT('%', :keyword, '%'))
            ORDER BY v.createdAt DESC
            """)
    Page<Vehicle> findAdminVehicles(
            @Param("complexId") Long complexId,
            @Param("building") String building,
            @Param("unit") String unit,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 관리자 차량 목록 페이지 조회, 상태 필터 포함
    @Query("""
            SELECT v FROM Vehicle v
            LEFT JOIN HouseholdCache h ON h.id = v.householdId
            LEFT JOIN UserCache u ON u.id = v.userId
            WHERE v.complexId = :complexId
              AND v.isDeleted = false
              AND v.status = :status
              AND (:building IS NULL OR :building = '' OR h.building = :building)
              AND (:unit IS NULL OR :unit = '' OR h.unit = :unit)
              AND (:keyword IS NULL OR :keyword = ''
                   OR v.licensePlate LIKE CONCAT('%', :keyword, '%')
                   OR v.modelName LIKE CONCAT('%', :keyword, '%')
                   OR u.name LIKE CONCAT('%', :keyword, '%'))
            ORDER BY v.createdAt DESC
            """)
    Page<Vehicle> findAdminVehiclesByStatus(
            @Param("complexId") Long complexId,
            @Param("status") VehicleStatus status,
            @Param("building") String building,
            @Param("unit") String unit,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
