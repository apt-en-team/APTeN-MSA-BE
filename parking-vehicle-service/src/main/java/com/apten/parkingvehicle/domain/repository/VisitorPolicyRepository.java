package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.VisitorPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// visitor_policy 원본 테이블 접근을 담당하는 repository이다.
public interface VisitorPolicyRepository extends JpaRepository<VisitorPolicy, Long> {

    // 단지 ID 기준 방문차량 정책을 조회한다.
    Optional<VisitorPolicy> findByComplexId(Long complexId);
}
