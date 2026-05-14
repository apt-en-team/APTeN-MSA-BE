package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.RegularVisitorVehicle;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 고정 방문차량 원본 테이블 접근을 담당하는 저장소이다.
public interface RegularVisitorVehicleRepository extends JpaRepository<RegularVisitorVehicle, Long> {

    // 사용자 기준 활성 고정 방문차량 목록을 조회한다.
    List<RegularVisitorVehicle> findByUserIdAndIsDeletedFalse(Long userId);

    // 고정 방문차량 식별자와 미삭제 조건으로 조회한다.
    Optional<RegularVisitorVehicle> findByIdAndIsDeletedFalse(Long id);

    // 단지와 차량번호 기준 입출차 시점에 유효한 고정 방문차량을 조회한다.
    // 활성 + 미삭제 + 시작일 이전 + 종료일 이후(또는 미설정) 조건을 모두 만족해야 한다.
    @Query("SELECT r FROM RegularVisitorVehicle r " +
            "WHERE r.complexId = :complexId " +
            "AND r.licensePlate = :licensePlate " +
            "AND r.isActive = true " +
            "AND r.isDeleted = false " +
            "AND r.startDate <= :date " +
            "AND (r.endDate IS NULL OR r.endDate >= :date)")
    Optional<RegularVisitorVehicle> findActiveByComplexAndPlateAndDate(
            @Param("complexId") Long complexId,
            @Param("licensePlate") String licensePlate,
            @Param("date") LocalDate date
    );
}