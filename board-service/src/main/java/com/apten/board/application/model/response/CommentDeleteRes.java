package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 삭제 응답이다.
@Getter
@Builder
public class CommentDeleteRes {
    private final Long commentId;
    private final LocalDateTime deletedAt;
}
