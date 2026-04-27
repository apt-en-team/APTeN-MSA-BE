package com.apten.board.application.model.response;

import com.apten.board.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 생성 응답이다.
@Getter
@Builder
public class VoteCreateRes {
    private final Long voteId;
    private final Long noticeId;
    private final String title;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final VoteStatus status;
    private final LocalDateTime createdAt;
}
