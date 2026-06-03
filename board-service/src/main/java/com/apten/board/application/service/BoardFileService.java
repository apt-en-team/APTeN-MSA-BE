package com.apten.board.application.service;

import com.apten.board.application.model.request.FileUploadReq;
import com.apten.board.application.model.response.FileUploadRes;
import com.apten.board.domain.entity.BoardFile;
import com.apten.board.domain.entity.BoardPost;
import com.apten.board.domain.entity.NoticeFile;
import com.apten.board.domain.enums.BoardFileType;
import com.apten.board.domain.repository.BoardFileRepository;
import com.apten.board.domain.repository.BoardPostRepository;
import com.apten.board.domain.repository.NoticeFileRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardFileService {

    private final BoardFileRepository boardFileRepository;
    private final BoardPostRepository boardPostRepository;
    private final NoticeFileRepository noticeFileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public FileUploadRes uploadBoardFiles(FileUploadReq request) {
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new BusinessException(BoardErrorCode.FILE_EMPTY);
        }

        BoardPost post = boardPostRepository.findByIdAndComplexIdAndIsDeletedFalse(request.getPostId(), currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));

        List<FileUploadRes.FileItem> fileItems = new ArrayList<>();
        int sortOrder = 0;

        for (MultipartFile file : request.getFiles()) {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(BoardErrorCode.FILE_EMPTY);
            }
            if (file.getSize() > 20L * 1024 * 1024) {
                throw new BusinessException(BoardErrorCode.FILE_SIZE_EXCEEDED);
            }

            String savedName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            BoardFileType fileType = resolveFileType(file);

            // 실제 파일 저장
            try {
                Path dirPath = Paths.get(uploadDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                Path filePath = dirPath.resolve(savedName);
                file.transferTo(filePath.toFile());
            } catch (IOException e) {
                throw new BusinessException(BoardErrorCode.FILE_SAVE_FAILED);
            }

            String filePath = "/boards/files/" + savedName;

            BoardFile boardFile = boardFileRepository.save(BoardFile.builder()
                    .postId(post.getId())
                    .originName(file.getOriginalFilename())
                    .savedName(savedName)
                    .filePath(filePath)
                    .fileType(fileType)
                    .fileSize(file.getSize())
                    .sortOrder(sortOrder++)
                    .build());

            fileItems.add(FileUploadRes.FileItem.builder()
                    .fileId(boardFile.getId())
                    .postId(boardFile.getPostId())
                    .originName(boardFile.getOriginName())
                    .savedName(boardFile.getSavedName())
                    .filePath(boardFile.getFilePath())
                    .fileType(boardFile.getFileType())
                    .fileSize(boardFile.getFileSize())
                    .sortOrder(boardFile.getSortOrder())
                    .build());
        }

        return FileUploadRes.builder()
                .files(fileItems)
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    private BoardFileType resolveFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return BoardFileType.IMAGE;
        }
        return BoardFileType.FILE;
    }

    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }

    @Transactional
    public FileUploadRes uploadNoticeFiles(Long noticeId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(BoardErrorCode.FILE_EMPTY);
        }

        List<FileUploadRes.FileItem> fileItems = new ArrayList<>();
        int sortOrder = 0;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(BoardErrorCode.FILE_EMPTY);
            }
            if (file.getSize() > 20L * 1024 * 1024) {
                throw new BusinessException(BoardErrorCode.FILE_SIZE_EXCEEDED);
            }

            String savedName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            BoardFileType fileType = resolveFileType(file);

            try {
                Path dirPath = Paths.get(uploadDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                Path filePath = dirPath.resolve(savedName);
                file.transferTo(filePath.toFile());
            } catch (IOException e) {
                throw new BusinessException(BoardErrorCode.FILE_SAVE_FAILED);
            }

            String filePath = "/notices/files/" + savedName;

            NoticeFile noticeFile = noticeFileRepository.save(NoticeFile.builder()
                    .noticeId(noticeId)
                    .originName(file.getOriginalFilename())
                    .savedName(savedName)
                    .filePath(filePath)
                    .fileType(fileType)
                    .fileSize(file.getSize())
                    .sortOrder(sortOrder++)
                    .build());

            fileItems.add(FileUploadRes.FileItem.builder()
                    .fileId(noticeFile.getId())
                    .originName(noticeFile.getOriginName())
                    .savedName(noticeFile.getSavedName())
                    .filePath(noticeFile.getFilePath())
                    .fileType(noticeFile.getFileType())
                    .fileSize(noticeFile.getFileSize())
                    .sortOrder(noticeFile.getSortOrder())
                    .build());
        }

        return FileUploadRes.builder()
                .files(fileItems)
                .uploadedAt(LocalDateTime.now())
                .build();
    }
}