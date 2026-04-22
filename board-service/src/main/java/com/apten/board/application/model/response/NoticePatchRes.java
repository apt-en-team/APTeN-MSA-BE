package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 수정 응답 DTO
@Getter
@Builder
public class NoticePatchRes {
    private final String boardUid;
    private final String title;
    private final Boolean isPinned;
    private final LocalDateTime updatedAt;
}
