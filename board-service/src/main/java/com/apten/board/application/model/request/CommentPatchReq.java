package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 댓글 수정 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPatchReq {

    // 수정할 댓글 본문이다.
    private String content;
}
