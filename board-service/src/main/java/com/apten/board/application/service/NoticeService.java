package com.apten.board.application.service;

import com.apten.board.application.model.request.NoticeCreateReq;
import com.apten.board.application.model.request.NoticeListReq;
import com.apten.board.application.model.request.NoticePatchReq;
import com.apten.board.application.model.response.NoticeCreateRes;
import com.apten.board.application.model.response.NoticeDeleteRes;
import com.apten.board.application.model.response.NoticeDetailRes;
import com.apten.board.application.model.response.NoticeListRes;
import com.apten.board.application.model.response.NoticePatchRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.domain.entity.Notice;
import com.apten.board.domain.entity.NoticeFile;
import com.apten.board.domain.entity.UserCache;
import com.apten.board.domain.enums.BoardFileType;
import com.apten.board.domain.repository.NoticeFileRepository;
import com.apten.board.domain.repository.NoticeRepository;
import com.apten.board.domain.repository.UserCacheRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.board.infrastructure.kafka.BoardOutboxService;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import com.apten.common.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserCacheRepository userCacheRepository;
    private final BoardOutboxService boardOutboxService;
    private final NoticeFileRepository noticeFileRepository;

    // 공지 작성
    @Transactional
    public NoticeCreateRes createNotice(NoticeCreateReq request) {
        Notice notice = noticeRepository.save(Notice.builder()
                .complexId(currentComplexId())
                .userId(currentUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .build());

        boardOutboxService.saveNoticeCreatedEvent(notice);

        return NoticeCreateRes.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    // 공지 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<NoticeListRes> getNoticeList(NoticeListReq request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        Page<Notice> page = noticeRepository.findByComplexIdAndIsDeletedFalseOrderByCreatedAtDesc(currentComplexId(), pageable);

        return PageResponse.<NoticeListRes>builder()
                .content(page.getContent().stream().map(this::toNoticeListRes).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 공지 상세 조회
    @Transactional(readOnly = true)
    public NoticeDetailRes getNoticeDetail(Long noticeId) {
        Notice notice = getNotice(noticeId);
        return toNoticeDetailRes(notice);
    }

    // 공지 수정
    @Transactional
    public NoticePatchRes updateNotice(Long noticeId, NoticePatchReq request) {
        Notice notice = getNotice(noticeId);
        validateNoticeWriter(notice);
        notice.update(request.getTitle(), request.getContent());

        return NoticePatchRes.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    // 공지 삭제
    @Transactional
    public NoticeDeleteRes deleteNotice(Long noticeId) {
        Notice notice = getNotice(noticeId);
        validateNoticeWriter(notice);
        notice.markDeleted();

        return NoticeDeleteRes.builder()
                .noticeId(notice.getId())
                .deletedAt(notice.getDeletedAt())
                .build();
    }

    // 관리자 공지 목록 조회 (삭제된 공지 포함)
    @Transactional(readOnly = true)
    public PageResponse<NoticeListRes> getAdminNoticeList(NoticeListReq request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        Page<Notice> page = noticeRepository.findByComplexIdOrderByCreatedAtDesc(currentComplexId(), pageable);

        return PageResponse.<NoticeListRes>builder()
                .content(page.getContent().stream().map(this::toNoticeListRes).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 관리자 공지 상세 조회
    @Transactional(readOnly = true)
    public NoticeDetailRes getAdminNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findByIdAndComplexId(noticeId, currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.NOTICE_NOT_FOUND));
        return toNoticeDetailRes(notice);
    }

    private Notice getNotice(Long noticeId) {
        return noticeRepository.findByIdAndComplexIdAndIsDeletedFalse(noticeId, currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.NOTICE_NOT_FOUND));
    }

    // MASTER는 작성자 관계없이 수정/삭제 가능
    private void validateNoticeWriter(Notice notice) {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
        if (userContext.getUserRole() == UserRole.MASTER) {
            return;
        }
        if (!notice.getUserId().equals(currentUserId())) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
    }

    private NoticeListRes toNoticeListRes(Notice notice) {
        String writerName = userCacheRepository.findById(notice.getUserId())
                .map(UserCache::getName)
                .orElse("알 수 없음");

        List<NoticeFile> files = noticeFileRepository.findByNoticeIdOrderBySortOrderAsc(notice.getId());
        String thumbSavedName = files.stream()
                .filter(f -> f.getFileType() == BoardFileType.IMAGE)
                .findFirst()
                .map(NoticeFile::getSavedName)
                .orElse(null);
        boolean hasFile = files.stream().anyMatch(f -> f.getFileType() == BoardFileType.FILE);

        return NoticeListRes.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .writerName(writerName)
                .createdAt(notice.getCreatedAt())
                .thumbSavedName(thumbSavedName)
                .hasFile(hasFile)
                .isDeleted(notice.getIsDeleted())
                .build();
    }

    private NoticeDetailRes toNoticeDetailRes(Notice notice) {
        String writerName = userCacheRepository.findById(notice.getUserId())
                .map(UserCache::getName)
                .orElse("알 수 없음");

        return NoticeDetailRes.builder()
                .noticeId(notice.getId())
                .complexId(notice.getComplexId())
                .userId(notice.getUserId())
                .writerName(writerName)
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .isDeleted(notice.getIsDeleted())
                .files(noticeFileRepository.findByNoticeIdOrderBySortOrderAsc(notice.getId()).stream()
                        .map(file -> NoticeDetailRes.FileItem.builder()
                                .fileId(file.getId())
                                .noticeId(file.getNoticeId())
                                .originName(file.getOriginName())
                                .savedName(file.getSavedName())
                                .filePath(file.getFilePath())
                                .fileType(file.getFileType())
                                .fileSize(file.getFileSize())
                                .sortOrder(file.getSortOrder())
                                .build())
                        .toList())
                .build();
    }

    private Long currentUserId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getUserId() == null) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
        return userContext.getUserId();
    }

    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }

    private Pageable buildPageable(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : size;
        return PageRequest.of(safePage, safeSize);
    }
}