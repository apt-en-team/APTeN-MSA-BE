package com.apten.board.application.controller;

import com.apten.board.application.model.request.CommentSearchReq;
import com.apten.board.application.model.request.FreeBoardPatchReq;
import com.apten.board.application.model.request.FreeBoardPostReq;
import com.apten.board.application.model.request.FreeBoardSearchReq;
import com.apten.board.application.model.response.BoardLikeToggleRes;
import com.apten.board.application.model.response.BoardViewPostRes;
import com.apten.board.application.model.response.FreeBoardDeleteRes;
import com.apten.board.application.model.response.FreeBoardGetDetailRes;
import com.apten.board.application.model.response.FreeBoardGetPageRes;
import com.apten.board.application.model.response.FreeBoardPatchRes;
import com.apten.board.application.model.response.FreeBoardPostRes;
import com.apten.board.application.model.response.MyBoardGetPageRes;
import com.apten.board.application.service.FreeBoardService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 자유게시판 API 진입점
// 게시글 목록과 상세, 작성과 수정, 삭제, 조회수와 좋아요 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class FreeBoardController {

    // 자유게시판 응용 서비스
    private final FreeBoardService freeBoardService;

    // 자유게시판 목록 조회 API
    @GetMapping("/free")
    public ResultResponse<FreeBoardGetPageRes> getFreeBoardList(@ModelAttribute FreeBoardSearchReq request) {
        return ResultResponse.success("자유게시판 목록 조회 성공", freeBoardService.getFreeBoardList(request));
    }

    // 자유게시판 상세 조회 API
    @GetMapping("/free/{boardUid}")
    public ResultResponse<FreeBoardGetDetailRes> getFreeBoardDetail(@PathVariable String boardUid) {
        return ResultResponse.success("자유게시판 상세 조회 성공", freeBoardService.getFreeBoardDetail(boardUid));
    }

    // 자유게시판 등록 API
    @PostMapping("/free")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FreeBoardPostRes> createFreeBoard(@RequestBody FreeBoardPostReq request) {
        return ResultResponse.success("자유게시판 등록 성공", freeBoardService.createFreeBoard(request));
    }

    // 자유게시판 수정 API
    @PatchMapping("/free/{boardUid}")
    public ResultResponse<FreeBoardPatchRes> updateFreeBoard(
            @PathVariable String boardUid,
            @RequestBody FreeBoardPatchReq request
    ) {
        return ResultResponse.success("자유게시판 수정 성공", freeBoardService.updateFreeBoard(boardUid, request));
    }

    // 자유게시판 삭제 API
    @DeleteMapping("/free/{boardUid}")
    public ResultResponse<FreeBoardDeleteRes> deleteFreeBoard(@PathVariable String boardUid) {
        return ResultResponse.success("자유게시판 삭제 성공", freeBoardService.deleteFreeBoard(boardUid));
    }

    // 내 게시글 조회 API
    @GetMapping("/my-posts")
    public ResultResponse<MyBoardGetPageRes> getMyBoardList(@ModelAttribute CommentSearchReq request) {
        return ResultResponse.success("내 게시글 조회 성공", freeBoardService.getMyBoardList(request));
    }

    // 게시글 조회수 증가 API
    @PostMapping("/{boardUid}/view")
    public ResultResponse<BoardViewPostRes> increaseBoardView(@PathVariable String boardUid) {
        return ResultResponse.success("게시글 조회수 증가 성공", freeBoardService.increaseBoardView(boardUid));
    }

    // 게시글 좋아요 토글 API
    @PostMapping("/{boardUid}/likes")
    public ResultResponse<BoardLikeToggleRes> toggleBoardLike(@PathVariable String boardUid) {
        return ResultResponse.success("게시글 좋아요 토글 성공", freeBoardService.toggleBoardLike(boardUid));
    }
}
