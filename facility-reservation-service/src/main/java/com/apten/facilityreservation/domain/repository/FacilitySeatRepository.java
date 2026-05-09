package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilitySeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 좌석 저장소이다.
public interface FacilitySeatRepository extends JpaRepository<FacilitySeat, Long> {

    // 시설별 좌석 목록을 조회한다.
    List<FacilitySeat> findByFacilityId(Long facilityId);

    // 시설별 활성 좌석 목록을 조회한다.
    List<FacilitySeat> findByFacilityIdAndIsActiveTrue(Long facilityId);

    // 좌석 번호 중복 여부를 확인한다.
    boolean existsByFacilityIdAndSeatNo(Long facilityId, Integer seatNo);
}
