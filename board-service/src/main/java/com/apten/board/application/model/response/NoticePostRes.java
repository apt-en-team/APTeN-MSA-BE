package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 등록 응답 DTO
@Getter
@Builder
public class NoticePostRes {
    private final Long boardId;
    private final String boardUid;
    private final String title;
    private final Boolean isPinned;
    private final LocalDateTime createdAt;
}
