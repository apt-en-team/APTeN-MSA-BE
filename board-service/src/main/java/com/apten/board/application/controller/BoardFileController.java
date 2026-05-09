package com.apten.board.application.controller;

import com.apten.board.application.model.request.FileUploadReq;
import com.apten.board.application.model.response.FileUploadRes;
import com.apten.board.application.service.BoardFileService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 첨부파일 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/files")
public class BoardFileController {

    // 첨부파일 서비스이다.
    private final BoardFileService boardFileService;

    //첨부파일 업로드 API-521
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FileUploadRes> uploadBoardFiles(@ModelAttribute FileUploadReq request) {
        return ResultResponse.success("첨부파일 업로드 성공", boardFileService.uploadBoardFiles(request));
    }
}
