package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.VehiclePolicy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// vehicle_policy 원본 테이블 접근을 담당하는 repository이다.
public interface VehiclePolicyRepository extends JpaRepository<VehiclePolicy, Long> {

    // 단지 ID 기준 차량 정책 목록을 조회한다.
    List<VehiclePolicy> findByComplexId(Long complexId);

    // 단지 ID 기준 기존 차량 정책을 전체 삭제한다.
    void deleteByComplexId(Long complexId);
}
