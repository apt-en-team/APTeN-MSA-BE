package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 댓글 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPostReq {
    private String content;
}
