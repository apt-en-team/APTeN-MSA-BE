package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 등록 응답 DTO
@Getter
@Builder
public class CommentPostRes {
    private final Long commentId;
    private final String commentUid;
    private final String boardUid;
    private final String writerName;
    private final LocalDateTime createdAt;
}
