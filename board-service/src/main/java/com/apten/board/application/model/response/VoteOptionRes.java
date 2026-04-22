package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 투표 상세 선택지 응답 DTO
@Getter
@Builder
public class VoteOptionRes {
    private final String optionUid;
    private final String optionText;
    private final Integer voteCount;
}
