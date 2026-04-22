package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 수정 응답 DTO
@Getter
@Builder
public class CommentPatchRes {
    private final String commentUid;
    private final String content;
    private final LocalDateTime updatedAt;
}
