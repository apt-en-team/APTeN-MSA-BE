package com.apten.board.application.service;

import com.apten.board.application.model.request.BoardStatisticsReq;
import com.apten.board.application.model.request.MyPostListReq;
import com.apten.board.application.model.request.PopularPostListReq;
import com.apten.board.application.model.request.PostCreateReq;
import com.apten.board.application.model.request.PostListReq;
import com.apten.board.application.model.request.PostPatchReq;
import com.apten.board.application.model.response.AdminPostDeleteRes;
import com.apten.board.application.model.response.BoardStatisticsRes;
import com.apten.board.application.model.response.MyPostListRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.model.response.PopularPostListRes;
import com.apten.board.application.model.response.PostCreateRes;
import com.apten.board.application.model.response.PostDeleteRes;
import com.apten.board.application.model.response.PostDetailRes;
import com.apten.board.application.model.response.PostLikeToggleRes;
import com.apten.board.application.model.response.PostListRes;
import com.apten.board.application.model.response.PostPatchRes;
import com.apten.board.domain.entity.BoardPost;
import com.apten.board.domain.entity.UserCache;
import com.apten.board.domain.enums.UserCacheStatus;
import com.apten.board.domain.repository.BoardFileRepository;
import com.apten.board.domain.repository.BoardLikeRepository;
import com.apten.board.domain.repository.BoardPostRepository;
import com.apten.board.domain.repository.BoardCommentRepository;
import com.apten.board.domain.repository.NoticeRepository;
import com.apten.board.domain.repository.UserCacheRepository;
import com.apten.board.domain.repository.VoteRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.board.infrastructure.kafka.BoardOutboxService;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 게시글 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class FreeBoardService {

    // 게시글 저장소이다.
    private final BoardPostRepository boardPostRepository;

    // 게시글 첨부파일 저장소이다.
    private final BoardFileRepository boardFileRepository;

    // 좋아요 저장소이다.
    private final BoardLikeRepository boardLikeRepository;

    // 사용자 캐시 저장소이다.
    private final UserCacheRepository userCacheRepository;

    // 댓글 저장소이다.
    private final BoardCommentRepository boardCommentRepository;

    // 공지 저장소이다.
    private final NoticeRepository noticeRepository;

    // 투표 저장소이다.
    private final VoteRepository voteRepository;

    // 게시판 outbox 서비스이다.
    private final BoardOutboxService boardOutboxService;

    //게시글 작성
    @Transactional
    public PostCreateRes createPost(PostCreateReq request) {
        UserCache writer = getCurrentActiveUser();

        BoardPost post = boardPostRepository.save(
                BoardPost.builder()
                        .complexId(resolveCurrentComplexId(writer))
                        .userId(writer.getId())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build()
        );

        boardOutboxService.savePostCreatedEvent(post);

        return PostCreateRes.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .createdAt(post.getCreatedAt())
                .build();
    }

    //게시글 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<PostListRes> getPostList(PostListReq request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        Page<BoardPost> page = boardPostRepository.findByComplexIdAndIsDeletedFalse(currentComplexId(), pageable);

        return PageResponse.<PostListRes>builder()
                .content(page.getContent().stream().map(this::toPostListRes).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    //게시글 상세 조회
    @Transactional
    public PostDetailRes getPostDetail(Long postId) {
        BoardPost post = getPost(postId);
        post.increaseViewCount();

        return PostDetailRes.builder()
                .postId(post.getId())
                .complexId(post.getComplexId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .files(boardFileRepository.findByPostIdOrderBySortOrderAsc(post.getId()).stream()
                        .map(file -> PostDetailRes.FileItem.builder()
                                .fileId(file.getId())
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

    //게시글 수정
    @Transactional
    public PostPatchRes updatePost(Long postId, PostPatchReq request) {
        BoardPost post = getPost(postId);
        validatePostWriter(post);
        post.update(request.getTitle(), request.getContent());

        return PostPatchRes.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    //게시글 삭제
    @Transactional
    public PostDeleteRes deletePost(Long postId) {
        BoardPost post = getPost(postId);
        validatePostWriter(post);
        post.markDeleted();

        return PostDeleteRes.builder()
                .postId(post.getId())
                .deletedAt(post.getDeletedAt())
                .build();
    }

    //좋아요 등록 또는 취소
    @Transactional
    public PostLikeToggleRes togglePostLike(Long postId) {
        BoardPost post = getPost(postId);
        Long userId = currentUserId();

        boolean exists = boardLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (exists) {
            boardLikeRepository.deleteByPostIdAndUserId(postId, userId);
            post.decreaseLikeCount();
        } else {
            boardLikeRepository.save(com.apten.board.domain.entity.BoardLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build());
            post.increaseLikeCount();
        }

        return PostLikeToggleRes.builder()
                .postId(post.getId())
                .liked(!exists)
                .likeCount(post.getLikeCount())
                .build();
    }

    //내 게시글 조회
    @Transactional(readOnly = true)
    public PageResponse<MyPostListRes> getMyPostList(MyPostListReq request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        Page<BoardPost> page = boardPostRepository.findByUserIdAndIsDeletedFalse(currentUserId(), pageable);

        return PageResponse.<MyPostListRes>builder()
                .content(page.getContent().stream().map(post -> MyPostListRes.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .viewCount(post.getViewCount())
                        .likeCount(post.getLikeCount())
                        .createdAt(post.getCreatedAt())
                        .build()).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    //인기글 조회
    @Transactional(readOnly = true)
    public List<PopularPostListRes> getPopularPostList(PopularPostListReq request) {
        //TODO 인기글 기준 기간과 정렬 조건을 QueryDSL 또는 Mapper로 최적화
        return boardPostRepository.findTop10ByComplexIdAndIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(currentComplexId())
                .stream()
                .limit(Math.max(1, request.getSize()))
                .map(post -> PopularPostListRes.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .likeCount(post.getLikeCount())
                        .viewCount(post.getViewCount())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();
    }

    //관리자 게시글 강제 삭제
    @Transactional
    public AdminPostDeleteRes forceDeletePost(Long postId) {
        BoardPost post = getPost(postId);
        post.markDeleted();

        return AdminPostDeleteRes.builder()
                .postId(post.getId())
                .deletedAt(post.getDeletedAt())
                .build();
    }

    //게시판 통계 조회
    @Transactional(readOnly = true)
    public BoardStatisticsRes getBoardStatistics(BoardStatisticsReq request) {
        //TODO 기간 조건이 필요하면 createdAt 범위와 복합 쿼리로 최적화
        Long complexId = currentComplexId();

        return BoardStatisticsRes.builder()
                .postCount(boardPostRepository.countByComplexIdAndIsDeletedFalse(complexId))
                .commentCount(boardCommentRepository.countByIsDeletedFalse())
                .noticeCount(noticeRepository.countByComplexIdAndIsDeletedFalse(complexId))
                .voteCount(voteRepository.countByComplexId(complexId))
                .build();
    }

    //현재 사용자 기준 단지 게시글을 조회한다.
    private BoardPost getPost(Long postId) {
        return boardPostRepository.findByIdAndComplexIdAndIsDeletedFalse(postId, currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));
    }

    //게시글 작성자 검증을 수행한다.
    private void validatePostWriter(BoardPost post) {
        if (!post.getUserId().equals(currentUserId())) {
            throw new BusinessException(BoardErrorCode.POST_WRITER_MISMATCH);
        }
    }

    //현재 요청 사용자 캐시를 조회한다.
    private UserCache getCurrentActiveUser() {
        return userCacheRepository.findByIdAndStatus(currentUserId(), UserCacheStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(BoardErrorCode.USER_NOT_FOUND));
    }

    //현재 사용자 ID를 반환한다.
    private Long currentUserId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getUserId() == null) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
        return userContext.getUserId();
    }

    //현재 단지 ID를 반환한다.
    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }

    //컨텍스트 단지 ID가 없으면 사용자 캐시 값을 사용한다.
    private Long resolveCurrentComplexId(UserCache userCache) {
        UserContext userContext = UserContextHolder.get();
        if (userContext != null && userContext.getComplexId() != null) {
            return userContext.getComplexId();
        }
        return userCache.getComplexId();
    }

    //페이지 요청을 안전하게 만든다.
    private Pageable buildPageable(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : size;
        return PageRequest.of(safePage, safeSize);
    }

    //게시글 목록 응답으로 변환한다.
    private PostListRes toPostListRes(BoardPost post) {
        return PostListRes.builder()
                .postId(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
