package com.apten.household.domain.repository;

import com.apten.household.domain.entity.VehicleSnapshot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 차량 비용 반영용 스냅샷 저장소이다.
public interface VehicleSnapshotRepository extends JpaRepository<VehicleSnapshot, Long> {

    // 세대 ID 기준 차량 스냅샷 목록을 조회한다.
    List<VehicleSnapshot> findByHouseholdId(Long householdId);
}
