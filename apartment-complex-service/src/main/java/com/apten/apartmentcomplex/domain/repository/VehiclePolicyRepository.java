package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.VehiclePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiclePolicyRepository extends JpaRepository<VehiclePolicy, Long> {

    List<VehiclePolicy> findByComplexId(Long complexId);

    void deleteByComplexId(Long complexId);
}
