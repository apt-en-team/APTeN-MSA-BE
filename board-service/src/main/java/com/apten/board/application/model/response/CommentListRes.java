package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentListRes {
    private final Long commentId;
    private final Long postId;
    private final Long userId;
    private final String writerName;
    private final String userRole;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}