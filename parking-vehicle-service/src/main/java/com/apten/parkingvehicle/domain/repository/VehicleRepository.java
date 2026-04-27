package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 입주민 차량 원본 테이블 접근을 담당하는 저장소이다.
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 단지 기준 차량번호 중복 여부를 조회한다.
    boolean existsByComplexIdAndLicensePlateAndIsDeletedFalse(Long complexId, String licensePlate);

    // 세대 기준 승인 차량 수를 조회한다.
    long countByHouseholdIdAndStatusAndIsDeletedFalse(Long householdId, VehicleStatus status);

    // 사용자 기준 차량 목록을 조회한다.
    List<Vehicle> findByUserIdAndIsDeletedFalse(Long userId);

    // 차량 식별자와 미삭제 조건으로 차량을 조회한다.
    Optional<Vehicle> findByIdAndIsDeletedFalse(Long id);

    // 단지와 상태 기준 차량 목록을 조회한다.
    List<Vehicle> findByComplexIdAndStatusAndIsDeletedFalse(Long complexId, VehicleStatus status);
}
