package com.apten.board.application.controller;

import com.apten.board.application.model.request.BoardStatisticsReq;
import com.apten.board.application.model.request.PostListReq;
import com.apten.board.application.model.response.*;
import com.apten.board.application.service.CommentService;
import com.apten.board.application.service.FreeBoardService;
import com.apten.board.domain.enums.BoardCategory;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 관리자 게시판 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/boards")
public class AdminBoardController {

    // 게시글 서비스이다.
    private final FreeBoardService freeBoardService;

    // 댓글 서비스이다.
    private final CommentService commentService;

    //게시글 강제 삭제
    @DeleteMapping("/posts/{postId}")
    public ResultResponse<AdminPostDeleteRes> forceDeletePost(@PathVariable Long postId) {
        return ResultResponse.success("게시글 강제 삭제 성공", freeBoardService.forceDeletePost(postId));
    }

    //댓글 강제 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResultResponse<AdminCommentDeleteRes> forceDeleteComment(@PathVariable Long commentId) {
        return ResultResponse.success("댓글 강제 삭제 성공", commentService.forceDeleteComment(commentId));
    }

    // 관리자 게시글 상세 조회 (삭제 여부 무관)
    @GetMapping("/posts/{postId}")
    public ResultResponse<PostDetailRes> getPostDetail(@PathVariable Long postId) {
        return ResultResponse.success("게시글 상세 조회 성공", freeBoardService.getAdminPostDetail(postId));
    }

    // 관리자 게시글 목록 조회 (삭제된 글 포함)
    @GetMapping("/posts")
    public ResultResponse<PageResponse<PostListRes>> getAdminPostList(
            @RequestParam(required = false) BoardCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PostListReq request = PostListReq.builder()
                .category(category)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
        return ResultResponse.success("관리자 게시글 목록 조회 성공", freeBoardService.getAdminPostList(request));
    }

    //게시판 통계 조회
    @GetMapping("/statistics")
    public ResultResponse<BoardStatisticsRes> getBoardStatistics(@ModelAttribute BoardStatisticsReq request) {
        return ResultResponse.success("게시판 통계 조회 성공", freeBoardService.getBoardStatistics(request));
    }
}
