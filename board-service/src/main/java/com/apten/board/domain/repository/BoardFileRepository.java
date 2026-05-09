package com.apten.board.domain.repository;

import com.apten.board.domain.entity.BoardFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 게시글 첨부파일 메타데이터 저장소이다.
public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {

    // 게시글 첨부파일을 표시 순서대로 조회한다.
    List<BoardFile> findByPostIdOrderBySortOrderAsc(Long postId);
}
