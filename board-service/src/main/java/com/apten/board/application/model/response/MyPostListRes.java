package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 내 게시글 목록 응답 아이템이다.
@Getter
@Builder
public class MyPostListRes {
    private final Long postId;
    private final String title;
    private final Integer viewCount;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
}
