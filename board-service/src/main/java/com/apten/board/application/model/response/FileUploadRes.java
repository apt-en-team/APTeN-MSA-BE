package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardFileType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 첨부파일 업로드 응답이다.
@Getter
@Builder
public class FileUploadRes {
    private final List<FileItem> files;
    private final LocalDateTime uploadedAt;

    // 업로드된 파일 메타데이터 응답이다.
    @Getter
    @Builder
    public static class FileItem {
        private final Long fileId;
        private final Long postId;
        private final String originName;
        private final String savedName;
        private final String filePath;
        private final BoardFileType fileType;
        private final Long fileSize;
        private final Integer sortOrder;
    }
}
