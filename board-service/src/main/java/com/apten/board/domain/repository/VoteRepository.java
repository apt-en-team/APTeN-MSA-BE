package com.apten.board.domain.repository;

import com.apten.board.domain.entity.Vote;
import com.apten.board.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 투표 저장소이다.
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 단지 기준 투표 상세 조회에 사용한다.
    Optional<Vote> findByIdAndComplexId(Long id, Long complexId);

    // 단지 기준 투표 목록을 최신순으로 조회한다. (관리자용)
    Page<Vote> findByComplexIdOrderByCreatedAtDesc(Long complexId, Pageable pageable);

    // 단지 + 상태 기준 투표 목록을 최신순으로 조회한다. (관리자용 + 입주민 결과발표 탭)
    Page<Vote> findByComplexIdAndStatusOrderByCreatedAtDesc(Long complexId, VoteStatus status, Pageable pageable);

    // 입주민 진행중 탭 — OPEN 상태이거나 READY이면서 시작일이 지난 투표를 조회한다.
    @Query("SELECT v FROM Vote v WHERE v.complexId = :complexId " +
            "AND (v.status = :open OR (v.status = :ready AND v.startAt <= :now)) " +
            "ORDER BY v.createdAt DESC")
    Page<Vote> findActiveVotes(@Param("complexId") Long complexId,
                               @Param("now") LocalDateTime now,
                               @Param("open") VoteStatus open,
                               @Param("ready") VoteStatus ready,
                               Pageable pageable);

    // 단지 기준 전체 투표 수를 구한다.
    long countByComplexId(Long complexId);

    // 단지 + 상태 기준 투표 수를 구한다.
    long countByComplexIdAndStatus(Long complexId, VoteStatus status);
}