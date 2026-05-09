package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 내 댓글 목록 응답 아이템이다.
@Getter
@Builder
public class MyCommentListRes {
    private final Long commentId;
    private final Long postId;
    private final String content;
    private final LocalDateTime createdAt;
}
