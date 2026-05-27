package com.apten.board.application.model.request;

import com.apten.board.domain.enums.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateReq {

    private BoardCategory category;
    private String title;
    private String content;
    private List<FileItemReq> files;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileItemReq {
        private String fileType;
        private String originName;
        private String savedName;
        private String filePath;
        private Long fileSize;
        private Integer sortOrder;
    }
}