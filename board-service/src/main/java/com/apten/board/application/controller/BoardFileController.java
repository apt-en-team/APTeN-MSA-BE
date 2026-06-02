package com.apten.board.application.controller;

import com.apten.board.application.model.request.FileUploadReq;
import com.apten.board.application.model.response.FileUploadRes;
import com.apten.board.application.service.BoardFileService;
import com.apten.board.domain.repository.BoardFileRepository;
import com.apten.board.domain.repository.NoticeFileRepository;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class BoardFileController {

    private final BoardFileService boardFileService;
    private final BoardFileRepository boardFileRepository;
    private final NoticeFileRepository noticeFileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 첨부파일 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FileUploadRes> uploadBoardFiles(
            @RequestParam("postId") Long postId,
            @RequestParam("files") List<MultipartFile> files) {
        FileUploadReq request = FileUploadReq.builder()
                .postId(postId)
                .files(files)
                .build();
        return ResultResponse.success("첨부파일 업로드 성공", boardFileService.uploadBoardFiles(request));
    }

    // 첨부파일 서빙
    @GetMapping("/serve/{savedName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String savedName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(savedName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String originName = boardFileRepository.findBySavedName(savedName)
                    .map(f -> f.getOriginName())
                    .orElseGet(() -> noticeFileRepository.findBySavedName(savedName)
                            .map(f -> f.getOriginName())
                            .orElse(savedName));

            String encodedName = java.net.URLEncoder.encode(originName, java.nio.charset.StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 공지 첨부파일 업로드
    @PostMapping(value = "/notices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FileUploadRes> uploadNoticeFiles(
            @RequestParam("noticeId") Long noticeId,
            @RequestParam("files") List<MultipartFile> files) {
        return ResultResponse.success("공지 첨부파일 업로드 성공",
                boardFileService.uploadNoticeFiles(noticeId, files));
    }
}