package com.apten.board.application.controller;

import com.apten.board.application.model.request.CommentPatchReq;
import com.apten.board.application.model.request.CommentPostReq;
import com.apten.board.application.model.request.CommentSearchReq;
import com.apten.board.application.model.response.CommentDeleteRes;
import com.apten.board.application.model.response.CommentGetPageRes;
import com.apten.board.application.model.response.CommentPatchRes;
import com.apten.board.application.model.response.CommentPostRes;
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

// 댓글 API 진입점
// 댓글 목록과 등록, 수정, 삭제 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class CommentController {

    // 댓글 응용 서비스
    private final CommentService commentService;

    // 댓글 목록 조회 API
    @GetMapping("/api/boards/{boardUid}/comments")
    public ResultResponse<CommentGetPageRes> getCommentList(
            @PathVariable String boardUid,
            @ModelAttribute CommentSearchReq request
    ) {
        return ResultResponse.success("댓글 목록 조회 성공", commentService.getCommentList(boardUid, request));
    }

    // 댓글 등록 API
    @PostMapping("/api/boards/{boardUid}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<CommentPostRes> createComment(
            @PathVariable String boardUid,
            @RequestBody CommentPostReq request
    ) {
        return ResultResponse.success("댓글 등록 성공", commentService.createComment(boardUid, request));
    }

    // 댓글 수정 API
    @PatchMapping("/api/comments/{commentUid}")
    public ResultResponse<CommentPatchRes> updateComment(
            @PathVariable String commentUid,
            @RequestBody CommentPatchReq request
    ) {
        return ResultResponse.success("댓글 수정 성공", commentService.updateComment(commentUid, request));
    }

    // 댓글 삭제 API
    @DeleteMapping("/api/comments/{commentUid}")
    public ResultResponse<CommentDeleteRes> deleteComment(@PathVariable String commentUid) {
        return ResultResponse.success("댓글 삭제 성공", commentService.deleteComment(commentUid));
    }
}
