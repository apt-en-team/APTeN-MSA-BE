package com.apten.board.application.controller;

import com.apten.board.application.model.request.CommentCreateReq;
import com.apten.board.application.model.request.CommentListReq;
import com.apten.board.application.model.request.CommentPatchReq;
import com.apten.board.application.model.request.MyCommentListReq;
import com.apten.board.application.model.response.CommentCreateRes;
import com.apten.board.application.model.response.CommentDeleteRes;
import com.apten.board.application.model.response.CommentListRes;
import com.apten.board.application.model.response.CommentPatchRes;
import com.apten.board.application.model.response.MyCommentListRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.service.CommentService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 댓글 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class CommentController {

    // 댓글 서비스이다.
    private final CommentService commentService;

    //댓글 작성 API-506
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<CommentCreateRes> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateReq request
    ) {
        return ResultResponse.success("댓글 작성 성공", commentService.createComment(postId, request));
    }

    //댓글 수정 API-507
    @PatchMapping("/comments/{commentId}")
    public ResultResponse<CommentPatchRes> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentPatchReq request
    ) {
        return ResultResponse.success("댓글 수정 성공", commentService.updateComment(commentId, request));
    }

    //댓글 삭제 API-508
    @DeleteMapping("/comments/{commentId}")
    public ResultResponse<CommentDeleteRes> deleteComment(@PathVariable Long commentId) {
        return ResultResponse.success("댓글 삭제 성공", commentService.deleteComment(commentId));
    }

    //내 댓글 조회 API-523
    @GetMapping("/my-comments")
    public ResultResponse<PageResponse<MyCommentListRes>> getMyCommentList(@ModelAttribute MyCommentListReq request) {
        return ResultResponse.success("내 댓글 조회 성공", commentService.getMyCommentList(request));
    }

    //댓글 목록 조회 API-537
    @GetMapping("/posts/{postId}/comments")
    public ResultResponse<PageResponse<CommentListRes>> getCommentList(
            @PathVariable Long postId,
            @ModelAttribute CommentListReq request
    ) {
        return ResultResponse.success("댓글 목록 조회 성공", commentService.getCommentList(postId, request));
    }
}
