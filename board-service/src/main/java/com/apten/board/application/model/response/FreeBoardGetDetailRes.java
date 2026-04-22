package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 자유게시판 상세 응답 DTO
@Getter
@Builder
public class FreeBoardGetDetailRes {
    private final Long boardId;
    private final String boardUid;
    private final String title;
    private final String content;
    private final BoardWriterRes writer;
    private final Integer viewCount;
    private final Integer commentCount;
    private final List<BoardAttachmentRes> attachments;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
