package com.apten.board.domain.repository;

import com.apten.board.domain.entity.Vote;
import com.apten.board.domain.enums.VoteStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 투표 저장소이다.
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 단지 기준 투표 상세 조회에 사용한다.
    Optional<Vote> findByIdAndComplexId(Long id, Long complexId);

    // 단지 기준 투표 목록을 조회한다.
    Page<Vote> findByComplexId(Long complexId, Pageable pageable);

    // 상태 기준 투표 목록을 조회한다.
    Page<Vote> findByComplexIdAndStatus(Long complexId, VoteStatus status, Pageable pageable);

    // 관리자 통계용 투표 수를 구한다.
    long countByComplexId(Long complexId);
}
