package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// complex-policy-service의 저장과 단순 조회를 맡는 JPA Repository
public interface ComplexPolicyRepository extends JpaRepository<ComplexPolicy, Long> {
    // 단지 ID 기준으로 기존 기본 정책을 조회한다
    Optional<ComplexPolicy> findByComplexId(Long complexId);
}
