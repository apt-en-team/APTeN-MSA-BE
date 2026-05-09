package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 게시글 작성 응답이다.
@Getter
@Builder
public class PostCreateRes {
    private final Long postId;
    private final String title;
    private final LocalDateTime createdAt;
}
