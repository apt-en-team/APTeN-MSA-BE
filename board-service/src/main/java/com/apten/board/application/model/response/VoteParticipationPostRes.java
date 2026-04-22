package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 참여 응답 DTO
@Getter
@Builder
public class VoteParticipationPostRes {
    private final String voteUid;
    private final String optionUid;
    private final LocalDateTime participatedAt;
}
