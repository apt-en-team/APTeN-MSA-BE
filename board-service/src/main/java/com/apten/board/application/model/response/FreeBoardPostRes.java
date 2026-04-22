package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 자유게시판 등록 응답 DTO
@Getter
@Builder
public class FreeBoardPostRes {
    private final Long boardId;
    private final String boardUid;
    private final String title;
    private final String writerName;
    private final LocalDateTime createdAt;
}
