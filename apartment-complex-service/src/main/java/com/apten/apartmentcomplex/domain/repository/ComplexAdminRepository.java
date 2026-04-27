package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// complex_admin 테이블 접근을 담당하는 JPA repository
public interface ComplexAdminRepository extends JpaRepository<ComplexAdmin, Long> {

    // 단지 ID와 관리자 사용자 ID 기준으로 기존 배정 이력을 조회한다.
    Optional<ComplexAdmin> findByComplexIdAndAdminUserId(Long complexId, Long adminUserId);

    // 단지 ID 기준 현재 활성화된 관리자 목록을 조회한다.
    List<ComplexAdmin> findByComplexIdAndIsActiveTrue(Long complexId);
}
