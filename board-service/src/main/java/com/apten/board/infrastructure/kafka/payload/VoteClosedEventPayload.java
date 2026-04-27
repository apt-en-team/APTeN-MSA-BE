package com.apten.board.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 종료 이벤트 payload이다.
@Getter
@Builder
public class VoteClosedEventPayload {
    private final Long voteId;
    private final Long complexId;
    private final String title;
    private final LocalDateTime closedAt;
}
