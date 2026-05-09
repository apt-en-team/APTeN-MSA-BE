package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.Facility;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 저장소이다.
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // 단지 기준 활성 시설 목록을 조회한다.
    List<Facility> findByComplexIdAndIsDeletedFalse(Long complexId);

    // 단지 기준 삭제되지 않은 시설을 상세 조회한다.
    Optional<Facility> findByIdAndComplexIdAndIsDeletedFalse(Long id, Long complexId);

    // 삭제되지 않은 시설을 조회한다.
    Optional<Facility> findByIdAndIsDeletedFalse(Long id);
}
