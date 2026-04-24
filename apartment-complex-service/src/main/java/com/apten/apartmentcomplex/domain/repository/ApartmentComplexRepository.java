package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// apartment-complex-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper 아래의 MyBatis 인터페이스로 분리한다
public interface ApartmentComplexRepository extends JpaRepository<ApartmentComplex, Long> {

    // API에서 전달받은 단지 UID를 엔티티의 code 기준으로 조회한다
    Optional<ApartmentComplex> findByCode(String code);

    //단지 중복 체크
    boolean existsByName(String name);

    //단지 코드 생성에 필요한 마지막 단지코드 조회
    @Query("select c.code from ApartmentComplex c where c.code like 'APT-%' order by c.code desc limit 1")
    Optional<String> findLastCode();

    // 관리자 단지 목록에서 키워드로 단지명 또는 주소를 검색하고 페이징 처리한다.
    @Query("""
            SELECT c
            FROM ApartmentComplex c
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR c.name LIKE CONCAT('%', :keyword, '%')
                   OR c.address LIKE CONCAT('%', :keyword, '%'))
            ORDER BY c.id DESC
            """)
    Page<ApartmentComplex> findPageByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
