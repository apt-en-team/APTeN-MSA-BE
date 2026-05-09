package com.apten.board.application.controller;

import com.apten.board.application.model.request.VoteListReq;
import com.apten.board.application.model.request.VoteParticipationReq;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.model.response.VoteDetailRes;
import com.apten.board.application.model.response.VoteListRes;
import com.apten.board.application.model.response.VoteParticipationRes;
import com.apten.board.application.service.VoteService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 투표 조회 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/votes")
public class VoteController {

    // 투표 서비스이다.
    private final VoteService voteService;

    //투표 참여 API-516
    @PostMapping("/{voteId}/participations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VoteParticipationRes> participateVote(
            @PathVariable Long voteId,
            @RequestBody VoteParticipationReq request
    ) {
        return ResultResponse.success("투표 참여 성공", voteService.participateVote(voteId, request));
    }

    //투표 목록 조회 API-530
    @GetMapping
    public ResultResponse<PageResponse<VoteListRes>> getVoteList(@ModelAttribute VoteListReq request) {
        return ResultResponse.success("투표 목록 조회 성공", voteService.getVoteList(request));
    }

    //투표 상세 조회 API-531
    @GetMapping("/{voteId}")
    public ResultResponse<VoteDetailRes> getVoteDetail(@PathVariable Long voteId) {
        return ResultResponse.success("투표 상세 조회 성공", voteService.getVoteDetail(voteId));
    }
}
