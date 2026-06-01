package com.apten.household.domain.repository;

import com.apten.household.domain.entity.VehicleSnapshot;
import com.apten.household.domain.enums.VehicleSnapshotStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleSnapshotRepository extends JpaRepository<VehicleSnapshot, Long> {

    List<VehicleSnapshot> findByHouseholdId(Long householdId);

    List<VehicleSnapshot> findByComplexIdAndStatusAndIsDeletedFalse(Long complexId, VehicleSnapshotStatus status);
}
