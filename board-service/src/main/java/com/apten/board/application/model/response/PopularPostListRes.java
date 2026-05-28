package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularPostListRes {
    private final Long postId;
    private final String title;
    private final String writerName;
    private final Integer commentCount;
    private final Integer likeCount;
    private final Integer viewCount;
    private final LocalDateTime createdAt;
}