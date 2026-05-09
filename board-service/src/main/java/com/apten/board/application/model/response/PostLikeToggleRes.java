package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 게시글 좋아요 토글 응답이다.
@Getter
@Builder
public class PostLikeToggleRes {
    private final Long postId;
    private final Boolean liked;
    private final Integer likeCount;
}
