package com.apten.board.application.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

// 게시글 첨부파일 업로드 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadReq {

    // 연결할 게시글 ID이다.
    private Long postId;

    // 업로드할 파일 목록이다.
    private List<MultipartFile> files;
}
