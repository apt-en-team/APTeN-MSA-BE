package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 게시글과 공지의 첨부파일 응답 DTO
@Getter
@Builder
public class BoardAttachmentRes {
    private final String fileUid;
    private final String fileName;
    private final String fileUrl;
}
