package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import org.springframework.data.jpa.repository.JpaRepository;


// complex_admin 테이블 접근을 담당하는 JPA repository
public interface ComplexAdminRepository extends JpaRepository<ComplexAdmin, Long> {
}
