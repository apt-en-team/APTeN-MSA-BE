package com.apten.board.application.controller;

import com.apten.board.application.model.request.NoticeCreateReq;
import com.apten.board.application.model.request.NoticeListReq;
import com.apten.board.application.model.request.NoticePatchReq;
import com.apten.board.application.model.response.NoticeCreateRes;
import com.apten.board.application.model.response.NoticeDeleteRes;
import com.apten.board.application.model.response.NoticeDetailRes;
import com.apten.board.application.model.response.NoticeListRes;
import com.apten.board.application.model.response.NoticePatchRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.service.NoticeService;
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

// 관리자 공지 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {

    // 공지 서비스이다.
    private final NoticeService noticeService;

    //공지 작성 API-510
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<NoticeCreateRes> createNotice(@RequestBody NoticeCreateReq request) {
        return ResultResponse.success("공지 작성 성공", noticeService.createNotice(request));
    }

    //공지 수정 API-513
    @PatchMapping("/{noticeId}")
    public ResultResponse<NoticePatchRes> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticePatchReq request
    ) {
        return ResultResponse.success("공지 수정 성공", noticeService.updateNotice(noticeId, request));
    }

    //공지 삭제 API-514
    @DeleteMapping("/{noticeId}")
    public ResultResponse<NoticeDeleteRes> deleteNotice(@PathVariable Long noticeId) {
        return ResultResponse.success("공지 삭제 성공", noticeService.deleteNotice(noticeId));
    }

    //관리자 공지 목록 조회 API-528
    @GetMapping
    public ResultResponse<PageResponse<NoticeListRes>> getAdminNoticeList(@ModelAttribute NoticeListReq request) {
        return ResultResponse.success("관리자 공지 목록 조회 성공", noticeService.getAdminNoticeList(request));
    }

    //관리자 공지 상세 조회 API-529
    @GetMapping("/{noticeId}")
    public ResultResponse<NoticeDetailRes> getAdminNoticeDetail(@PathVariable Long noticeId) {
        return ResultResponse.success("관리자 공지 상세 조회 성공", noticeService.getAdminNoticeDetail(noticeId));
    }
}
