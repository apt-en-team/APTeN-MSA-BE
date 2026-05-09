package com.apten.board.application.controller;

import com.apten.board.application.model.request.VoteCreateReq;
import com.apten.board.application.model.request.VoteListReq;
import com.apten.board.application.model.request.VotePatchReq;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.model.response.VoteCloseRes;
import com.apten.board.application.model.response.VoteCreateRes;
import com.apten.board.application.model.response.VoteDeleteRes;
import com.apten.board.application.model.response.VoteDetailRes;
import com.apten.board.application.model.response.VoteListRes;
import com.apten.board.application.model.response.VotePatchRes;
import com.apten.board.application.model.response.VoteResultRes;
import com.apten.board.application.service.VoteService;
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

// 관리자 투표 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/votes")
public class AdminVoteController {

    // 투표 서비스이다.
    private final VoteService voteService;

    //투표 생성 API-515
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VoteCreateRes> createVote(@RequestBody VoteCreateReq request) {
        return ResultResponse.success("투표 생성 성공", voteService.createVote(request));
    }

    //투표 결과 조회 API-517
    @GetMapping("/{voteId}/results")
    public ResultResponse<VoteResultRes> getVoteResult(@PathVariable Long voteId) {
        return ResultResponse.success("투표 결과 조회 성공", voteService.getVoteResult(voteId));
    }

    //관리자 투표 목록 조회 API-532
    @GetMapping
    public ResultResponse<PageResponse<VoteListRes>> getAdminVoteList(@ModelAttribute VoteListReq request) {
        return ResultResponse.success("관리자 투표 목록 조회 성공", voteService.getAdminVoteList(request));
    }

    //관리자 투표 상세 조회 API-533
    @GetMapping("/{voteId}")
    public ResultResponse<VoteDetailRes> getAdminVoteDetail(@PathVariable Long voteId) {
        return ResultResponse.success("관리자 투표 상세 조회 성공", voteService.getAdminVoteDetail(voteId));
    }

    //투표 수정 API-534
    @PatchMapping("/{voteId}")
    public ResultResponse<VotePatchRes> updateVote(@PathVariable Long voteId, @RequestBody VotePatchReq request) {
        return ResultResponse.success("투표 수정 성공", voteService.updateVote(voteId, request));
    }

    //투표 삭제 API-535
    @DeleteMapping("/{voteId}")
    public ResultResponse<VoteDeleteRes> deleteVote(@PathVariable Long voteId) {
        return ResultResponse.success("투표 삭제 성공", voteService.deleteVote(voteId));
    }

    //투표 종료 처리 API-536
    @PatchMapping("/{voteId}/close")
    public ResultResponse<VoteCloseRes> closeVote(@PathVariable Long voteId) {
        return ResultResponse.success("투표 종료 처리 성공", voteService.closeVote(voteId));
    }
}
