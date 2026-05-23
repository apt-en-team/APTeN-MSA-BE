package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingSensor;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 주차 센서 원본 테이블 접근을 담당하는 저장소
public interface ParkingSensorRepository extends JpaRepository<ParkingSensor, Long> {

    // 단지 기준 주차 센서 존재 여부 조회
    boolean existsByComplexIdAndIsDeletedFalse(Long complexId);

    // 단지 기준 활성 주차 센서 목록 조회
    List<ParkingSensor> findByComplexIdAndIsActiveTrueAndIsDeletedFalse(Long complexId);

    // 주차 구역 기준 주차 센서 목록 조회
    List<ParkingSensor> findByZoneIdAndIsDeletedFalse(Long zoneId);

    // sensorId로 주차 센서 단건 조회
    Optional<ParkingSensor> findByIdAndIsDeletedFalse(Long sensorId);

    // 주차 구역 기준 주차 센서 목록 조회 (자리 번호 오름차순)
    List<ParkingSensor> findByZoneIdAndIsDeletedFalseOrderBySpotNumberAsc(Long zoneId);

    // 단지와 sensorCode 조합 단건 조회 (미삭제 한정)
    Optional<ParkingSensor> findByComplexIdAndSensorCodeAndIsDeletedFalse(Long complexId, String sensorCode);

    // 단지 내 sensorCode 중복 여부 확인
    boolean existsByComplexIdAndSensorCodeAndIsDeletedFalse(Long complexId, String sensorCode);

    // zone 기준 미삭제 센서 존재 여부 확인
    boolean existsByZoneIdAndIsDeletedFalse(Long zoneId);

    // zone 기준 활성 미삭제 센서 수 조회 (사용 가능 자리 수 산출용)
    long countByZoneIdAndIsActiveTrueAndIsDeletedFalse(Long zoneId);

    // zone 내 spotNumber 중복 여부 확인
    boolean existsByZoneIdAndSpotNumberAndIsDeletedFalse(Long zoneId, String spotNumber);

    // 일괄 등록 시 단지 내 기존 sensorCode 중복 사전 검증
    @Query("SELECT s.sensorCode FROM ParkingSensor s " +
            "WHERE s.complexId = :complexId " +
            "AND s.sensorCode IN :sensorCodes " +
            "AND s.isDeleted = false")
    List<String> findSensorCodesByComplexIdAndSensorCodeInAndIsDeletedFalse(
            @Param("complexId") Long complexId,
            @Param("sensorCodes") Collection<String> sensorCodes
    );

    // 일괄 등록 시 zone 내 기존 spotNumber 중복 사전 검증
    @Query("SELECT s.spotNumber FROM ParkingSensor s " +
            "WHERE s.zoneId = :zoneId " +
            "AND s.spotNumber IN :spotNumbers " +
            "AND s.isDeleted = false")
    List<String> findSpotNumbersByZoneIdAndSpotNumberInAndIsDeletedFalse(
            @Param("zoneId") Long zoneId,
            @Param("spotNumbers") Collection<String> spotNumbers
    );
}
