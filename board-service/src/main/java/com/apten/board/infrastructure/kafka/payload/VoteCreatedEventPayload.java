package com.apten.board.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 생성 이벤트 payload이다.
@Getter
@Builder
public class VoteCreatedEventPayload {
    private final Long voteId;
    private final Long noticeId;
    private final Long complexId;
    private final String title;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDateTime createdAt;
}
