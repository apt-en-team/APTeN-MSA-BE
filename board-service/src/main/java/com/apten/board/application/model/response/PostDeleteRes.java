package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 게시글 삭제 응답이다.
@Getter
@Builder
public class PostDeleteRes {
    private final Long postId;
    private final LocalDateTime deletedAt;
}
