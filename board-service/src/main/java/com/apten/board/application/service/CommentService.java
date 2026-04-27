package com.apten.board.application.service;

import com.apten.board.application.model.request.CommentCreateReq;
import com.apten.board.application.model.request.CommentListReq;
import com.apten.board.application.model.request.CommentPatchReq;
import com.apten.board.application.model.request.MyCommentListReq;
import com.apten.board.application.model.response.AdminCommentDeleteRes;
import com.apten.board.application.model.response.CommentCreateRes;
import com.apten.board.application.model.response.CommentDeleteRes;
import com.apten.board.application.model.response.CommentListRes;
import com.apten.board.application.model.response.CommentPatchRes;
import com.apten.board.application.model.response.MyCommentListRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.domain.entity.BoardComment;
import com.apten.board.domain.entity.BoardPost;
import com.apten.board.domain.repository.BoardCommentRepository;
import com.apten.board.domain.repository.BoardPostRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.board.infrastructure.kafka.BoardOutboxService;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 댓글 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class CommentService {

    // 댓글 저장소이다.
    private final BoardCommentRepository boardCommentRepository;

    // 게시글 저장소이다.
    private final BoardPostRepository boardPostRepository;

    // 댓글 생성 outbox 적재 서비스이다.
    private final BoardOutboxService boardOutboxService;

    //댓글 작성
    @Transactional
    public CommentCreateRes createComment(Long postId, CommentCreateReq request) {
        BoardPost post = getPost(postId);

        BoardComment comment = boardCommentRepository.save(BoardComment.builder()
                .postId(postId)
                .userId(currentUserId())
                .content(request.getContent())
                .build());

        boardOutboxService.saveCommentCreatedEvent(comment, post.getComplexId());

        return CommentCreateRes.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    //댓글 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<CommentListRes> getCommentList(Long postId, CommentListReq request) {
        getPost(postId);

        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        java.util.List<CommentListRes> items = boardCommentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentListRes)
                .toList();
        Page<CommentListRes> page = new PageImpl<>(items, pageable, items.size());

        return PageResponse.<CommentListRes>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    //댓글 수정
    @Transactional
    public CommentPatchRes updateComment(Long commentId, CommentPatchReq request) {
        BoardComment comment = getComment(commentId);
        validateCommentWriter(comment);
        comment.update(request.getContent());

        return CommentPatchRes.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    //댓글 삭제
    @Transactional
    public CommentDeleteRes deleteComment(Long commentId) {
        BoardComment comment = getComment(commentId);
        validateCommentWriter(comment);
        comment.markDeleted();

        return CommentDeleteRes.builder()
                .commentId(comment.getId())
                .deletedAt(comment.getDeletedAt())
                .build();
    }

    //내 댓글 조회
    @Transactional(readOnly = true)
    public PageResponse<MyCommentListRes> getMyCommentList(MyCommentListReq request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        Page<BoardComment> page = boardCommentRepository.findByUserIdAndIsDeletedFalse(currentUserId(), pageable);

        return PageResponse.<MyCommentListRes>builder()
                .content(page.getContent().stream().map(comment -> MyCommentListRes.builder()
                        .commentId(comment.getId())
                        .postId(comment.getPostId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build()).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    //관리자 댓글 강제 삭제
    @Transactional
    public AdminCommentDeleteRes forceDeleteComment(Long commentId) {
        BoardComment comment = getComment(commentId);
        comment.markDeleted();

        return AdminCommentDeleteRes.builder()
                .commentId(comment.getId())
                .deletedAt(comment.getDeletedAt())
                .build();
    }

    //댓글을 조회한다.
    private BoardComment getComment(Long commentId) {
        return boardCommentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(BoardErrorCode.COMMENT_NOT_FOUND));
    }

    //댓글이 속한 게시글을 조회한다.
    private BoardPost getPost(Long postId) {
        return boardPostRepository.findByIdAndComplexIdAndIsDeletedFalse(postId, currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));
    }

    //댓글 작성자 검증이다.
    private void validateCommentWriter(BoardComment comment) {
        if (!comment.getUserId().equals(currentUserId())) {
            throw new BusinessException(BoardErrorCode.COMMENT_WRITER_MISMATCH);
        }
    }

    //현재 사용자 ID를 가져온다.
    private Long currentUserId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getUserId() == null) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
        return userContext.getUserId();
    }

    //현재 단지 ID를 가져온다.
    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }

    //페이지 요청을 안전하게 만든다.
    private Pageable buildPageable(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : size;
        return PageRequest.of(safePage, safeSize);
    }

    //댓글 목록 응답으로 변환한다.
    private CommentListRes toCommentListRes(BoardComment comment) {
        return CommentListRes.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
