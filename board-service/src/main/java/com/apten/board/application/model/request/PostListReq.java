package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 목록 조회 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListReq {

    // 검색어이다.
    private String keyword;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
