package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 인기글 목록 조회 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularPostListReq {

    // 조회 개수이다.
    @Builder.Default
    private Integer size = 10;
}
