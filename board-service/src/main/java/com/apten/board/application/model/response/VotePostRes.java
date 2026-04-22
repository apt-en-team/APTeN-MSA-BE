package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 등록 응답 DTO
@Getter
@Builder
public class VotePostRes {
    private final Long voteId;
    private final String voteUid;
    private final String title;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final LocalDateTime createdAt;
}
