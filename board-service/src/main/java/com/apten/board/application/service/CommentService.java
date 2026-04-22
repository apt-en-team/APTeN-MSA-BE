package com.apten.board.application.service;

import com.apten.board.application.model.request.CommentPatchReq;
import com.apten.board.application.model.request.CommentPostReq;
import com.apten.board.application.model.request.CommentSearchReq;
import com.apten.board.application.model.response.CommentDeleteRes;
import com.apten.board.application.model.response.CommentGetPageRes;
import com.apten.board.application.model.response.CommentPatchRes;
import com.apten.board.application.model.response.CommentPostRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 댓글 응용 서비스
// 댓글 조회와 등록, 수정, 삭제 시그니처를 이 서비스에 모아둔다
@Service
public class CommentService {

    // 댓글 목록 조회 서비스
    public CommentGetPageRes getCommentList(String boardUid, CommentSearchReq request) {
        // TODO: 댓글 목록 조회 로직 구현
        return CommentGetPageRes.empty(request.getPage(), request.getSize());
    }

    // 댓글 등록 서비스
    public CommentPostRes createComment(String boardUid, CommentPostReq request) {
        // TODO: 댓글 등록 로직 구현
        return CommentPostRes.builder()
                .boardUid(boardUid)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 댓글 수정 서비스
    public CommentPatchRes updateComment(String commentUid, CommentPatchReq request) {
        // TODO: 댓글 수정 로직 구현
        return CommentPatchRes.builder()
                .commentUid(commentUid)
                .content(request.getContent())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 댓글 삭제 서비스
    public CommentDeleteRes deleteComment(String commentUid) {
        // TODO: 댓글 삭제 로직 구현
        return CommentDeleteRes.builder()
                .message("댓글 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
