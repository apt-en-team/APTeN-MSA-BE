package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 투표 등록 이벤트 응답 DTO
@Getter
@Builder
public class VoteCreatedEventRes {
    private final boolean published;
    private final LocalDateTime publishedAt;
}
