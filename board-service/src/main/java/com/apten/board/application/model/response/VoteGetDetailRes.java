package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 투표 상세 응답 DTO
@Getter
@Builder
public class VoteGetDetailRes {
    private final Long voteId;
    private final String voteUid;
    private final String title;
    private final String description;
    private final List<VoteOptionRes> options;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final Boolean isParticipated;
    private final LocalDateTime createdAt;
}
