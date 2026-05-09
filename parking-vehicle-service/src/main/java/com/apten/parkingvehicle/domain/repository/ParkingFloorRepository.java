package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingFloor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 주차층 원본 테이블 접근을 담당하는 저장소이다.
public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {

    // 단지 기준 주차층 이름 중복 여부를 조회한다.
    boolean existsByComplexIdAndFloorName(Long complexId, String floorName);

    // 단지 기준 주차층 목록을 조회한다.
    List<ParkingFloor> findByComplexId(Long complexId);

    // 단지 기준 활성 주차층 목록을 조회한다.
    List<ParkingFloor> findByComplexIdAndIsActiveTrue(Long complexId);

    // 주차층 식별자와 단지 기준으로 주차층을 조회한다.
    Optional<ParkingFloor> findByIdAndComplexId(Long id, Long complexId);
}
