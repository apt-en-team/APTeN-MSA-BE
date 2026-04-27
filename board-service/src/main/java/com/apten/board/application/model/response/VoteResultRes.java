package com.apten.board.application.model.response;

import com.apten.board.domain.enums.VoteStatus;
import lombok.Builder;
import lombok.Getter;

// 투표 결과 응답이다.
@Getter
@Builder
public class VoteResultRes {
    private final Long voteId;
    private final String title;
    private final VoteStatus status;
    private final Integer agreeCount;
    private final Integer disagreeCount;
    private final Integer householdCount;
}
