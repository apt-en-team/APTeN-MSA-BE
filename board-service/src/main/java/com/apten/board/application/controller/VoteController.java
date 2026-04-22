package com.apten.board.application.controller;

import com.apten.board.application.model.request.VoteParticipationPostReq;
import com.apten.board.application.model.request.VoteSearchReq;
import com.apten.board.application.model.response.VoteGetDetailRes;
import com.apten.board.application.model.response.VoteGetPageRes;
import com.apten.board.application.model.response.VoteParticipationPostRes;
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

// 투표 조회와 참여 API 진입점
// 투표 목록과 상세, 참여 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/votes")
public class VoteController {

    // 투표 응용 서비스
    private final VoteService voteService;

    // 투표 목록 조회 API
    @GetMapping
    public ResultResponse<VoteGetPageRes> getVoteList(@ModelAttribute VoteSearchReq request) {
        return ResultResponse.success("투표 목록 조회 성공", voteService.getVoteList(request));
    }

    // 투표 상세 조회 API
    @GetMapping("/{voteUid}")
    public ResultResponse<VoteGetDetailRes> getVoteDetail(@PathVariable String voteUid) {
        return ResultResponse.success("투표 상세 조회 성공", voteService.getVoteDetail(voteUid));
    }

    // 투표 참여 API
    @PostMapping("/{voteUid}/participations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VoteParticipationPostRes> participateVote(
            @PathVariable String voteUid,
            @RequestBody VoteParticipationPostReq request
    ) {
        return ResultResponse.success("투표 참여 성공", voteService.participateVote(voteUid, request));
    }
}
