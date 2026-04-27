package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 게시글 목록 응답 아이템이다.
@Getter
@Builder
public class PostListRes {
    private final Long postId;
    private final Long userId;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
}
