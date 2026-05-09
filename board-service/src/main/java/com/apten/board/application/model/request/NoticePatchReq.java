package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공지 수정 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticePatchReq {

    // 수정할 제목이다.
    private String title;

    // 수정할 본문이다.
    private String content;
}
