package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 타입 저장/조회 Repository
public interface FacilityTypeRepository extends JpaRepository<FacilityType, Long> {

    // 시설 타입 코드 조회
    Optional<FacilityType> findByTypeCode(FacilityTypeCode typeCode);

    // 활성 시설 타입 목록 조회
    List<FacilityType> findByIsActiveTrue();

    // 시설 타입 코드 중복 확인
    boolean existsByTypeCode(FacilityTypeCode typeCode);

    // 활성 여부 기준 시설 타입 목록 조회
    List<FacilityType> findByIsActiveOrderByIdAsc(Boolean isActive);

    // 전체 시설 타입 목록 조회
    List<FacilityType> findAllByOrderByIdAsc();
}
