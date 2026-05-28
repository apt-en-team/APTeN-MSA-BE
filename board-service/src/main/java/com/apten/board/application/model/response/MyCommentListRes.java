package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyCommentListRes {
    private final Long commentId;
    private final Long postId;
    private final String postTitle;
    private final String content;
    private final LocalDateTime createdAt;
}