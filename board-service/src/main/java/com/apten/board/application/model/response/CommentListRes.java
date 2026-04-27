package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 목록 응답 아이템이다.
@Getter
@Builder
public class CommentListRes {
    private final Long commentId;
    private final Long postId;
    private final Long userId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
