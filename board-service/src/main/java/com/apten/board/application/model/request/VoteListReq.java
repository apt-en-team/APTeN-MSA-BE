package com.apten.board.application.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 투표 목록 조회 요청이다.
@Getter
@Setter
@NoArgsConstructor
public class VoteListReq {

    // 검색어이다.
    private String keyword;

    // 투표 상태 필터이다. (READY | OPEN | CLOSED)
    private String status;

    // 페이지 번호이다.
    private Integer page = 0;

    // 페이지 크기이다.
    private Integer size = 20;
}