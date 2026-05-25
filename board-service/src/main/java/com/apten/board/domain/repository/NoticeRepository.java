package com.apten.board.domain.repository;

import com.apten.board.domain.entity.Notice;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByComplexIdAndIsDeletedFalseOrderByCreatedAtDesc(Long complexId, Pageable pageable);

    Optional<Notice> findByIdAndComplexIdAndIsDeletedFalse(Long id, Long complexId);

    long countByComplexIdAndIsDeletedFalse(Long complexId);
}