package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 게시글 수정 응답이다.
@Getter
@Builder
public class PostPatchRes {
    private final Long postId;
    private final String title;
    private final String content;
    private final LocalDateTime updatedAt;
}
