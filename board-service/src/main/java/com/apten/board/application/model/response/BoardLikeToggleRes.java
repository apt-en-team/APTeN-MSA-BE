package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 게시글 좋아요 토글 응답 DTO
@Getter
@Builder
public class BoardLikeToggleRes {
    private final String boardUid;
    private final boolean isLiked;
    private final Integer likeCount;
}
