package com.apten.household.domain.repository;

import com.apten.household.domain.entity.BillPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지별 청구 기본 정책 원본 저장소이다.
public interface BillPolicyRepository extends JpaRepository<BillPolicy, Long> {

    // 단지 ID 기준 청구 정책을 조회한다.
    Optional<BillPolicy> findByComplexId(Long complexId);
}
