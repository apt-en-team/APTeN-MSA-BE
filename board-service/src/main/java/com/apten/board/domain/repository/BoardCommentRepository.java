package com.apten.board.domain.repository;

import com.apten.board.domain.entity.BoardComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 댓글 원본 저장소이다.
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    // 게시글 기준으로 삭제되지 않은 댓글을 생성일 순서로 조회한다.
    List<BoardComment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    // 댓글 ID 기준으로 삭제되지 않은 댓글을 조회한다.
    Optional<BoardComment> findByIdAndIsDeletedFalse(Long id);

    // 작성자 기준으로 삭제되지 않은 댓글을 페이지 조회한다.
    Page<BoardComment> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    // 관리자 통계용 댓글 수를 구한다.
    long countByIsDeletedFalse();
}
