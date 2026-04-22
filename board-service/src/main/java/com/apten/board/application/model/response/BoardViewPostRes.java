package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 게시글 조회수 증가 응답 DTO
@Getter
@Builder
public class BoardViewPostRes {
    private final String boardUid;
    private final Integer viewCount;
}
