package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 수정 응답 DTO
@Getter
@Builder
public class VotePatchRes {
    private final String voteUid;
    private final String title;
    private final LocalDateTime updatedAt;
}
