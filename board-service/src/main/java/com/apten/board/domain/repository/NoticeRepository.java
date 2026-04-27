package com.apten.board.domain.repository;

import com.apten.board.domain.entity.Notice;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 공지 저장소이다.
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 단지 기준 공지 목록을 조회한다.
    Page<Notice> findByComplexIdAndIsDeletedFalse(Long complexId, Pageable pageable);

    // 단지와 공지 ID로 삭제되지 않은 공지를 조회한다.
    Optional<Notice> findByIdAndComplexIdAndIsDeletedFalse(Long id, Long complexId);

    // 관리자 통계용 공지 수를 구한다.
    long countByComplexIdAndIsDeletedFalse(Long complexId);
}
