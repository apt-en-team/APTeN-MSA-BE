package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 작성 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateReq {

    // 게시글 제목이다.
    private String title;

    // 게시글 본문이다.
    private String content;
}
