package com.apten.board.application.controller;

import com.apten.board.application.model.request.NoticePatchReq;
import com.apten.board.application.model.request.NoticePostReq;
import com.apten.board.application.model.response.NoticeDeleteRes;
import com.apten.board.application.model.response.NoticePatchRes;
import com.apten.board.application.model.response.NoticePostRes;
import com.apten.board.application.service.NoticeService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 공지 관리 API 진입점
// 공지 작성과 수정, 삭제 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/boards/notices")
public class AdminNoticeController {

    // 공지 응용 서비스
    private final NoticeService noticeService;

    // 공지 등록 API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<NoticePostRes> createNotice(@RequestBody NoticePostReq request) {
        return ResultResponse.success("공지 등록 성공", noticeService.createNotice(request));
    }

    // 공지 수정 API
    @PatchMapping("/{boardUid}")
    public ResultResponse<NoticePatchRes> updateNotice(
            @PathVariable String boardUid,
            @RequestBody NoticePatchReq request
    ) {
        return ResultResponse.success("공지 수정 성공", noticeService.updateNotice(boardUid, request));
    }

    // 공지 삭제 API
    @DeleteMapping("/{boardUid}")
    public ResultResponse<NoticeDeleteRes> deleteNotice(@PathVariable String boardUid) {
        return ResultResponse.success("공지 삭제 성공", noticeService.deleteNotice(boardUid));
    }
}
