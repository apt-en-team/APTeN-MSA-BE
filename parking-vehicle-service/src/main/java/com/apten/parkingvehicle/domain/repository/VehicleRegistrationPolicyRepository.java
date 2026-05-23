package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.VehicleRegistrationPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// vehicle_registration_policy 원본 테이블 접근을 담당하는 repository이다.
public interface VehicleRegistrationPolicyRepository extends JpaRepository<VehicleRegistrationPolicy, Long> {

    // 단지 ID 기준 등록 한도 정책을 조회한다.
    Optional<VehicleRegistrationPolicy> findByComplexId(Long complexId);

    // 단지 ID 기준 활성 등록 한도 정책을 조회한다.
    Optional<VehicleRegistrationPolicy> findByComplexIdAndIsActiveTrue(Long complexId);
}
