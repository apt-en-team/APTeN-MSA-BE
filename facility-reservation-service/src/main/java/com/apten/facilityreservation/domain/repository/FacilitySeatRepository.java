package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilitySeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 좌석 저장/조회 Repository
public interface FacilitySeatRepository extends JpaRepository<FacilitySeat, Long> {

    // 활성 시설 좌석 조회
    java.util.Optional<FacilitySeat> findByIdAndFacilityIdAndIsActiveTrue(Long id, Long facilityId);

    // 활성 시설 좌석 목록 조회
    List<FacilitySeat> findByFacilityIdAndIsActiveTrue(Long facilityId);

    // 좌석 번호 중복 확인
    boolean existsByFacilityIdAndSeatNo(Long facilityId, Integer seatNo);

    // 좌석 번호 일괄 중복 확인
    List<FacilitySeat> findByFacilityIdAndSeatNoIn(Long facilityId, List<Integer> seatNos);

    // 시설 좌석 목록 조회 (삭제 제외)
    List<FacilitySeat> findByFacilityIdOrderBySeatNoAsc(Long facilityId);
}
