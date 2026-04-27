package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 삭제 응답이다.
@Getter
@Builder
public class VoteDeleteRes {
    private final Long voteId;
    private final LocalDateTime deletedAt;
}
