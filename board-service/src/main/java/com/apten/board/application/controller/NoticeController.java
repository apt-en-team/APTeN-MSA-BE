package com.apten.board.application.controller;

import com.apten.board.application.model.request.NoticeSearchReq;
import com.apten.board.application.model.response.NoticeGetDetailRes;
import com.apten.board.application.model.response.NoticeGetPageRes;
import com.apten.board.application.service.NoticeService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 공지 조회 API 진입점
// 비회원도 볼 수 있는 공지 목록과 상세 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/notices")
public class NoticeController {

    // 공지 응용 서비스
    private final NoticeService noticeService;

    // 공지 목록 조회 API
    @GetMapping
    public ResultResponse<NoticeGetPageRes> getNoticeList(@ModelAttribute NoticeSearchReq request) {
        return ResultResponse.success("공지 목록 조회 성공", noticeService.getNoticeList(request));
    }

    // 공지 상세 조회 API
    @GetMapping("/{boardUid}")
    public ResultResponse<NoticeGetDetailRes> getNoticeDetail(@PathVariable String boardUid) {
        return ResultResponse.success("공지 상세 조회 성공", noticeService.getNoticeDetail(boardUid));
    }
}
