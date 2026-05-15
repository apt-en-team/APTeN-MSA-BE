package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardCategory;
import com.apten.board.domain.enums.BoardFileType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDetailRes {
    private final Long postId;
    private final Long complexId;
    private final Long userId;
    private final String writerName;
    private final BoardCategory category;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<FileItem> files;

    @Getter
    @Builder
    public static class FileItem {
        private final Long fileId;
        private final String originName;
        private final String savedName;
        private final String filePath;
        private final BoardFileType fileType;
        private final Long fileSize;
        private final Integer sortOrder;
    }
}