package com.apten.board.application.service;

import com.apten.board.application.model.response.BoardFileUploadRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// 게시판 첨부파일 응용 서비스
// 파일 저장 구현 없이 업로드 시그니처와 응답 형태만 이 서비스가 가진다
@Service
public class BoardFileService {

    // 게시판 첨부파일 업로드 서비스
    public BoardFileUploadRes uploadBoardFile(MultipartFile file) {
        // TODO: 첨부파일 저장 로직 구현
        return BoardFileUploadRes.builder()
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();
    }
}
