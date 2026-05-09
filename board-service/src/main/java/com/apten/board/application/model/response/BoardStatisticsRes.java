package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 게시판 통계 응답이다.
@Getter
@Builder
public class BoardStatisticsRes {
    private final Long postCount;
    private final Long commentCount;
    private final Long noticeCount;
    private final Long voteCount;
}
