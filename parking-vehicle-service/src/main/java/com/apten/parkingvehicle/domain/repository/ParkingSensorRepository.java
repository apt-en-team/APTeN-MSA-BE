package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingSensor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 주차 센서 원본 테이블 접근을 담당하는 저장소
public interface ParkingSensorRepository extends JpaRepository<ParkingSensor, Long> {

    // 단지 기준 주차 센서 존재 여부 조회
    boolean existsByComplexIdAndIsDeletedFalse(Long complexId);

    // 단지 기준 활성 주차 센서 목록 조회
    List<ParkingSensor> findByComplexIdAndIsActiveTrueAndIsDeletedFalse(Long complexId);

    // 주차 구역 기준 주차 센서 목록 조회
    List<ParkingSensor> findByZoneIdAndIsDeletedFalse(Long zoneId);
}
