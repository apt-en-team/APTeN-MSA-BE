package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import org.springframework.data.jpa.repository.JpaRepository;

// apartment-complex-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper 아래의 MyBatis 인터페이스로 분리한다
public interface ApartmentComplexRepository extends JpaRepository<ApartmentComplex, Long> {
}
