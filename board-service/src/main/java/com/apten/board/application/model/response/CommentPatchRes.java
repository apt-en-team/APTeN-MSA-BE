package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 수정 응답이다.
@Getter
@Builder
public class CommentPatchRes {
    private final Long commentId;
    private final String content;
    private final LocalDateTime updatedAt;
}
