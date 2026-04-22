package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 게시판 첨부파일 업로드 응답 DTO
@Getter
@Builder
public class BoardFileUploadRes {
    private final String fileUid;
    private final String fileName;
    private final String fileUrl;
    private final Long fileSize;
    private final LocalDateTime uploadedAt;
}
