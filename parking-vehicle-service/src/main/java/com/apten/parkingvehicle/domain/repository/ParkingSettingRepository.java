package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지별 주차 운영 타입 설정 저장소
public interface ParkingSettingRepository extends JpaRepository<ParkingSetting, Long> {

    // 단지 ID로 주차 운영 타입 설정을 조회한다.
    Optional<ParkingSetting> findByComplexId(Long complexId);

    // 단지 ID로 주차 운영 타입 설정 존재 여부를 확인한다.
    boolean existsByComplexId(Long complexId);
}