package com.apten.board.application.controller;

import com.apten.board.application.model.response.BoardFileUploadRes;
import com.apten.board.application.service.BoardFileService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// 게시판 첨부파일 업로드 API 진입점
// 파일 저장 로직은 아직 없고 업로드 시그니처만 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/files")
public class BoardFileController {

    // 파일 응용 서비스
    private final BoardFileService boardFileService;

    // 게시판 첨부파일 업로드 API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<BoardFileUploadRes> uploadBoardFile(@RequestPart("file") MultipartFile file) {
        return ResultResponse.success("게시판 첨부파일 업로드 성공", boardFileService.uploadBoardFile(file));
    }
}
