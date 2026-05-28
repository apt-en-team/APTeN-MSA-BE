package com.apten.board.application.model.request;

import com.apten.board.domain.enums.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListReq {

    //검색어이다.
    private String keyword;

    private BoardCategory category;

    //페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;
}