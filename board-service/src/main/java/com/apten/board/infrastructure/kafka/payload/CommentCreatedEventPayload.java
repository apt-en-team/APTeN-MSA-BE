package com.apten.board.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 댓글 생성 이벤트 payload이다.
@Getter
@Builder
public class CommentCreatedEventPayload {
    private final Long commentId;
    private final Long postId;
    private final Long complexId;
    private final Long userId;       // 댓글 작성자
    private final Long postAuthorId; // 게시글 작성자 (알림 수신자)
    private final String content;
    private final LocalDateTime createdAt;
}
