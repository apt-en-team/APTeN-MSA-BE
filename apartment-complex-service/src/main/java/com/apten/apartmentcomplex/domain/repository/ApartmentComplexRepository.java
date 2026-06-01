package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 단지 저장/조회 Repository
public interface ApartmentComplexRepository extends JpaRepository<ApartmentComplex, Long> {

    // 단지 코드 조회
    Optional<ApartmentComplex> findByCode(String code);

    // 단지명 중복 확인
    boolean existsByName(String name);

    // 마지막 단지 코드 조회
    @Query("select c.code from ApartmentComplex c where c.code like 'APT-%' order by c.code desc limit 1")
    Optional<String> findLastCode();

    // 단지 목록 조회 (키워드)
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

    // 단지 목록 조회 (키워드 + 상태)
    @Query("""
            SELECT c
            FROM ApartmentComplex c
            WHERE c.status = :status
              AND (:keyword IS NULL OR :keyword = ''
                   OR c.name LIKE CONCAT('%', :keyword, '%')
                   OR c.address LIKE CONCAT('%', :keyword, '%'))
            ORDER BY c.id DESC
            """)
    Page<ApartmentComplex> findPageByKeywordAndStatus(
            @Param("keyword") String keyword,
            @Param("status") ApartmentComplexStatus status,
            Pageable pageable
    );

    // 공개 단지 목록 조회 (키워드 + 상태)
    @Query("""
            SELECT c
            FROM ApartmentComplex c
            WHERE c.status = :status
              AND (:keyword IS NULL OR :keyword = ''
                   OR c.name LIKE CONCAT('%', :keyword, '%')
                   OR c.address LIKE CONCAT('%', :keyword, '%'))
            ORDER BY c.name ASC, c.id DESC
            """)
    List<ApartmentComplex> findPublicListByKeyword(
            @Param("keyword") String keyword,
            @Param("status") ApartmentComplexStatus status
    );
}
