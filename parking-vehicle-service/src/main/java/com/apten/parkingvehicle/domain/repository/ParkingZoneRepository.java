package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingZone;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 주차 구역 원본 테이블 접근을 담당하는 저장소이다.
public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Long> {

    // 단지 기준 주차장명과 구역명 조합 중복 여부를 조회한다. 구역명이 NULL인 경우도 안전하게 비교한다.
    @Query("SELECT COUNT(p) > 0 FROM ParkingZone p " +
            "WHERE p.complexId = :complexId " +
            "AND p.areaName = :areaName " +
            "AND ((:zoneName IS NULL AND p.zoneName IS NULL) OR p.zoneName = :zoneName)")
    boolean existsByComplexAndAreaAndZoneName(
            @Param("complexId") Long complexId,
            @Param("areaName") String areaName,
            @Param("zoneName") String zoneName
    );

    // 단지 기준 주차 구역 목록을 조회한다.
    List<ParkingZone> findByComplexId(Long complexId);

    // 단지 기준 활성 주차 구역 목록을 조회한다.
    List<ParkingZone> findByComplexIdAndIsActiveTrue(Long complexId);

    // 주차 구역 식별자와 단지 기준으로 주차 구역을 조회한다.
    Optional<ParkingZone> findByIdAndComplexId(Long id, Long complexId);

    // 단지 기준 활성 주차 구역 개수를 조회한다.
    long countByComplexIdAndIsActiveTrue(Long complexId);

    // 단지 기준 주차 구역 존재 여부를 조회한다.
    boolean existsByComplexId(Long complexId);
}