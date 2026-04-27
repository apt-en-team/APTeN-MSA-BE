package com.apten.board.domain.repository;

import com.apten.board.domain.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

// 게시글 좋아요 저장소이다.
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    // 동일 사용자의 좋아요 존재 여부를 확인한다.
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 취소 시 사용한다.
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
