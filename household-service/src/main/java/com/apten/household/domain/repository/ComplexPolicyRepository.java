package com.apten.household.domain.repository;

import com.apten.household.domain.entity.ComplexPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지별 기본 관리비 정책 원본 저장소이다.
public interface ComplexPolicyRepository extends JpaRepository<ComplexPolicy, Long> {

    // 단지 ID 기준 청구 기본 정책을 조회한다.
    Optional<ComplexPolicy> findByComplexId(Long complexId);
}
