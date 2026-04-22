package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 목록 아이템 응답 DTO
@Getter
@Builder
public class NoticeGetRes {
    private final Long boardId;
    private final String boardUid;
    private final String title;
    private final String writerName;
    private final Integer viewCount;
    private final Boolean isPinned;
    private final LocalDateTime createdAt;
}
