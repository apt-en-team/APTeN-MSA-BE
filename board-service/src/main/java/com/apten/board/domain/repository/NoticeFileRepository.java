package com.apten.board.domain.repository;

import com.apten.board.domain.entity.NoticeFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 공지 첨부파일 메타데이터 저장소이다.
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {

    // 공지 첨부파일을 표시 순서대로 조회한다.
    List<NoticeFile> findByNoticeIdOrderBySortOrderAsc(Long noticeId);
}
