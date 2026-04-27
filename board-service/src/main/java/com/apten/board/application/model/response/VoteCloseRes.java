package com.apten.board.application.model.response;

import com.apten.board.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 종료 응답이다.
@Getter
@Builder
public class VoteCloseRes {
    private final Long voteId;
    private final VoteStatus status;
    private final LocalDateTime closedAt;
}
