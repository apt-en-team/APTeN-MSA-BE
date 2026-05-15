package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardCategory;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostListRes {
    private final Long postId;
    private final Long userId;
    private final String writerName;
    private final BoardCategory category;
    private final String title;
    private final Integer viewCount;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
}