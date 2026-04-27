package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardFileType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 게시글 상세 응답이다.
@Getter
@Builder
public class PostDetailRes {
    private final Long postId;
    private final Long complexId;
    private final Long userId;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<FileItem> files;

    // 첨부파일 응답 아이템이다.
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
