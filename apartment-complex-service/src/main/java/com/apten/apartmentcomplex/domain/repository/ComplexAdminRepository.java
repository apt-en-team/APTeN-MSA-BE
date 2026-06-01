package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 관리자 소속 저장/조회 Repository
public interface ComplexAdminRepository extends JpaRepository<ComplexAdmin, Long> {

    // 관리자 소속 조회
    Optional<ComplexAdmin> findByComplexIdAndAdminUserId(Long complexId, Long adminUserId);

    // 단지 관리자 목록 조회 (최근 배정순)
    List<ComplexAdmin> findByComplexIdOrderByAssignedAtDesc(Long complexId);
}
