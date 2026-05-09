package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 작성 응답이다.
@Getter
@Builder
public class CommentCreateRes {
    private final Long commentId;
    private final Long postId;
    private final String content;
    private final LocalDateTime createdAt;
}
