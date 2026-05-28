package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPostListRes {
    private final Long postId;
    private final String writerName;
    private final String title;
    private final String preview;
    private final Integer viewCount;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
}