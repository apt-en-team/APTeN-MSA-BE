package com.apten.board.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 생성 이벤트 payload이다.
@Getter
@Builder
public class NoticeCreatedEventPayload {
    private final Long noticeId;
    private final Long complexId;
    private final Long userId;
    private final String title;
    private final LocalDateTime createdAt;
}
