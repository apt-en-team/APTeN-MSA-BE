package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityBlockTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 차단 시간 저장소이다.
public interface FacilityBlockTimeRepository extends JpaRepository<FacilityBlockTime, Long> {

    // 시설 기준 차단 시간 목록을 조회한다.
    List<FacilityBlockTime> findByFacilityId(Long facilityId);

    // 특정 날짜의 활성 차단 시간 목록을 조회한다.
    List<FacilityBlockTime> findByFacilityIdAndBlockDateAndIsActiveTrue(Long facilityId, LocalDate blockDate);
}
