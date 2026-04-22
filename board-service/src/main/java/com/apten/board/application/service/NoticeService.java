package com.apten.board.application.service;

import com.apten.board.application.model.request.NoticePatchReq;
import com.apten.board.application.model.request.NoticePostReq;
import com.apten.board.application.model.request.NoticeSearchReq;
import com.apten.board.application.model.response.NoticeDeleteRes;
import com.apten.board.application.model.response.NoticeGetDetailRes;
import com.apten.board.application.model.response.NoticeGetPageRes;
import com.apten.board.application.model.response.NoticePatchRes;
import com.apten.board.application.model.response.NoticePostRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 공지 응용 서비스
// 공지 조회와 등록, 수정, 삭제 시그니처를 이 서비스에 모아둔다
@Service
public class NoticeService {

    // 공지 목록 조회 서비스
    public NoticeGetPageRes getNoticeList(NoticeSearchReq request) {
        // TODO: 공지 목록 조회 로직 구현
        return NoticeGetPageRes.empty(request.getPage(), request.getSize());
    }

    // 공지 상세 조회 서비스
    public NoticeGetDetailRes getNoticeDetail(String boardUid) {
        // TODO: 공지 상세 조회 로직 구현
        return NoticeGetDetailRes.builder().build();
    }

    // 공지 등록 서비스
    public NoticePostRes createNotice(NoticePostReq request) {
        // TODO: 공지 등록 로직 구현
        // TODO: 공지 등록 알림 이벤트 발행
        return NoticePostRes.builder()
                .title(request.getTitle())
                .isPinned(request.getIsPinned())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 공지 수정 서비스
    public NoticePatchRes updateNotice(String boardUid, NoticePatchReq request) {
        // TODO: 공지 수정 로직 구현
        return NoticePatchRes.builder()
                .boardUid(boardUid)
                .title(request.getTitle())
                .isPinned(request.getIsPinned())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 공지 삭제 서비스
    public NoticeDeleteRes deleteNotice(String boardUid) {
        // TODO: 공지 삭제 로직 구현
        return NoticeDeleteRes.builder()
                .message("공지 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
