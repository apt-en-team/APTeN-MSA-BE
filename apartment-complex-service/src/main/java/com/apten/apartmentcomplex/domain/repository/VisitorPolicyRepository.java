package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.VisitorPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitorPolicyRepository extends JpaRepository<VisitorPolicy, Long> {
    Optional<VisitorPolicy> findByComplexId(Long complexId);
}
