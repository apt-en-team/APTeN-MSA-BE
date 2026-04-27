package com.apten.board.application.controller;

import com.apten.board.application.model.request.BoardStatisticsReq;
import com.apten.board.application.model.response.AdminCommentDeleteRes;
import com.apten.board.application.model.response.AdminPostDeleteRes;
import com.apten.board.application.model.response.BoardStatisticsRes;
import com.apten.board.application.service.CommentService;
import com.apten.board.application.service.FreeBoardService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 게시판 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/boards")
public class AdminBoardController {

    // 게시글 서비스이다.
    private final FreeBoardService freeBoardService;

    // 댓글 서비스이다.
    private final CommentService commentService;

    //게시글 강제 삭제 API-525
    @DeleteMapping("/posts/{postId}")
    public ResultResponse<AdminPostDeleteRes> forceDeletePost(@PathVariable Long postId) {
        return ResultResponse.success("게시글 강제 삭제 성공", freeBoardService.forceDeletePost(postId));
    }

    //댓글 강제 삭제 API-526
    @DeleteMapping("/comments/{commentId}")
    public ResultResponse<AdminCommentDeleteRes> forceDeleteComment(@PathVariable Long commentId) {
        return ResultResponse.success("댓글 강제 삭제 성공", commentService.forceDeleteComment(commentId));
    }

    //게시판 통계 조회 API-527
    @GetMapping("/statistics")
    public ResultResponse<BoardStatisticsRes> getBoardStatistics(@ModelAttribute BoardStatisticsReq request) {
        return ResultResponse.success("게시판 통계 조회 성공", freeBoardService.getBoardStatistics(request));
    }
}
