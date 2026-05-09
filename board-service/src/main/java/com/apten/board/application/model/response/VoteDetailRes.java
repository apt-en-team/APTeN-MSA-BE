package com.apten.board.application.model.response;

import com.apten.board.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 상세 응답이다.
@Getter
@Builder
public class VoteDetailRes {
    private final Long voteId;
    private final Long noticeId;
    private final Long complexId;
    private final String title;
    private final String description;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final VoteStatus status;
    private final Integer agreeCount;
    private final Integer disagreeCount;
    private final Integer householdCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
