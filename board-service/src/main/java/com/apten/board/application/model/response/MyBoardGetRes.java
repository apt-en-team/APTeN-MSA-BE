package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 내 게시글 목록 아이템 응답 DTO
@Getter
@Builder
public class MyBoardGetRes {
    private final Long boardId;
    private final String boardUid;
    private final String title;
    private final BoardType boardType;
    private final Integer commentCount;
    private final LocalDateTime createdAt;
}
