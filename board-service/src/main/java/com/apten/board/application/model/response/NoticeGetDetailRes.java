package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 공지 상세 응답 DTO
@Getter
@Builder
public class NoticeGetDetailRes {
    private final Long boardId;
    private final String boardUid;
    private final String title;
    private final String content;
    private final String writerName;
    private final Integer viewCount;
    private final Boolean isPinned;
    private final List<BoardAttachmentRes> attachments;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
