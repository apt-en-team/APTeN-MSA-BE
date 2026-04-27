package com.apten.board.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 게시글 생성 이벤트 payload이다.
@Getter
@Builder
public class PostCreatedEventPayload {
    private final Long postId;
    private final Long complexId;
    private final Long userId;
    private final String title;
    private final LocalDateTime createdAt;
}
