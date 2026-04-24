package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import com.apten.apartmentcomplex.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// complex_admin 테이블 접근을 담당하는 JPA repository이다.
public interface ComplexAdminRepository extends JpaRepository<ComplexAdmin, Long> {
}
