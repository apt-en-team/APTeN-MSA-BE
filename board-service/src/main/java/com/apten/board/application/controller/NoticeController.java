package com.apten.board.application.controller;

import com.apten.board.application.model.request.NoticeListReq;
import com.apten.board.application.model.response.NoticeDetailRes;
import com.apten.board.application.model.response.NoticeListRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.service.NoticeService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 공지 조회 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    // 공지 서비스이다.
    private final NoticeService noticeService;

    //공지 목록 조회 API-511
    @GetMapping
    public ResultResponse<PageResponse<NoticeListRes>> getNoticeList(@ModelAttribute NoticeListReq request) {
        return ResultResponse.success("공지 목록 조회 성공", noticeService.getNoticeList(request));
    }

    //공지 상세 조회 API-512
    @GetMapping("/{noticeId}")
    public ResultResponse<NoticeDetailRes> getNoticeDetail(@PathVariable Long noticeId) {
        return ResultResponse.success("공지 상세 조회 성공", noticeService.getNoticeDetail(noticeId));
    }
}
