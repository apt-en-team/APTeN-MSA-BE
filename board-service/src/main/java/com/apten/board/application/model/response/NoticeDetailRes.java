package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardFileType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeDetailRes {
    private final Long noticeId;
    private final Long complexId;
    private final Long userId;
    private final String writerName;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isDeleted;
    private final List<FileItem> files;

    @Getter
    @Builder
    public static class FileItem {
        private final Long fileId;
        private final Long noticeId;
        private final String originName;
        private final String savedName;
        private final String filePath;
        private final BoardFileType fileType;
        private final Long fileSize;
        private final Integer sortOrder;
    }
}