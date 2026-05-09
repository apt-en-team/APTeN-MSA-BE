package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 주차 입출차 로그 원본 테이블 접근을 담당하는 저장소이다.
public interface ParkingLogRepository extends JpaRepository<ParkingLog, Long> {

    // 단지와 차량번호 기준 가장 최근 로그를 조회한다.
    Optional<ParkingLog> findTopByComplexIdAndLicensePlateOrderByLoggedAtDesc(Long complexId, String licensePlate);

    // 단지와 연월 기준 로그 목록을 조회한다.
    List<ParkingLog> findByComplexId(Long complexId);
}
