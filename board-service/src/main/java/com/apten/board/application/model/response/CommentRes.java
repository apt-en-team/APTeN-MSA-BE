package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 목록 아이템 응답 DTO
@Getter
@Builder
public class CommentRes {
    private final Long commentId;
    private final String commentUid;
    private final String writerName;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
