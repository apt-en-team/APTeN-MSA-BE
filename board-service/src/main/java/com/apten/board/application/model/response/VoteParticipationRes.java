package com.apten.board.application.model.response;

import com.apten.board.domain.enums.VoteChoice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 참여 응답이다.
@Getter
@Builder
public class VoteParticipationRes {
    private final Long voteId;
    private final Long householdId;
    private final VoteChoice choice;
    private final LocalDateTime participatedAt;
}
