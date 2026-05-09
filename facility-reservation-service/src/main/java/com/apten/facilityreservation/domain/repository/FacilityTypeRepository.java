package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 타입 마스터 저장소이다.
public interface FacilityTypeRepository extends JpaRepository<FacilityType, Long> {

    // 타입 코드 기준 시설 타입을 조회한다.
    Optional<FacilityType> findByTypeCode(FacilityTypeCode typeCode);

    // 활성 시설 타입 목록을 조회한다.
    List<FacilityType> findByIsActiveTrue();

    // 동일 타입 코드 존재 여부를 확인한다.
    boolean existsByTypeCode(FacilityTypeCode typeCode);
}
