package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 수정 응답이다.
@Getter
@Builder
public class VotePatchRes {
    private final Long voteId;
    private final String title;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDateTime updatedAt;
}
