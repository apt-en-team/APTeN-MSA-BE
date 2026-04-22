package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 자유게시판 상세 작성자 응답 DTO
@Getter
@Builder
public class BoardWriterRes {
    private final String userUid;
    private final String name;
}
