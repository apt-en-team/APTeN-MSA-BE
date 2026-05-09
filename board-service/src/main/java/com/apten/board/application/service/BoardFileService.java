package com.apten.board.application.service;

import com.apten.board.application.model.request.FileUploadReq;
import com.apten.board.application.model.response.FileUploadRes;
import com.apten.board.domain.entity.BoardFile;
import com.apten.board.domain.entity.BoardPost;
import com.apten.board.domain.enums.BoardFileType;
import com.apten.board.domain.repository.BoardFileRepository;
import com.apten.board.domain.repository.BoardPostRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// 게시글 첨부파일 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class BoardFileService {

    // 게시글 첨부파일 저장소이다.
    private final BoardFileRepository boardFileRepository;

    // 게시글 저장소이다.
    private final BoardPostRepository boardPostRepository;

    //첨부파일 업로드
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

            //TODO 파일 실제 저장소 연동이 필요하면 S3 또는 로컬 저장 방식 확정
            String savedName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String filePath = "/uploads/boards/" + savedName;
            BoardFileType fileType = resolveFileType(file);

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

    //파일 유형을 판별한다.
    private BoardFileType resolveFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return BoardFileType.IMAGE;
        }
        return BoardFileType.FILE;
    }

    //현재 단지 ID를 가져온다.
    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }
}
