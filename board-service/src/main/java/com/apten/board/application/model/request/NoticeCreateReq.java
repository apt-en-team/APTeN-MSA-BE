package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공지 작성 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateReq {

    // 공지 제목이다.
    private String title;

    // 공지 본문이다.
    private String content;
}
