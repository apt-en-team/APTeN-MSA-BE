package com.apten.board.application.model.response;

import com.apten.board.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 목록 응답 아이템이다.
@Getter
@Builder
public class VoteListRes {
    private final Long voteId;
    private final String title;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final VoteStatus status;
    private final Integer agreeCount;
    private final Integer disagreeCount;
    private final Integer householdCount;
}
