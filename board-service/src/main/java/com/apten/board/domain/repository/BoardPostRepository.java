package com.apten.board.domain.repository;

import com.apten.board.domain.entity.BoardPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 게시글 원본 저장소이다.
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    // 단지 기준으로 삭제되지 않은 게시글을 조회한다.
    Page<BoardPost> findByComplexIdAndIsDeletedFalse(Long complexId, Pageable pageable);

    // 단지와 게시글 ID로 삭제되지 않은 게시글을 조회한다.
    Optional<BoardPost> findByIdAndComplexIdAndIsDeletedFalse(Long id, Long complexId);

    // 작성자 기준으로 삭제되지 않은 게시글을 조회한다.
    Page<BoardPost> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    // 인기글 조회에 사용한다.
    List<BoardPost> findTop10ByComplexIdAndIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(Long complexId);

    // 관리자 통계용 게시글 수를 구한다.
    long countByComplexIdAndIsDeletedFalse(Long complexId);
}
