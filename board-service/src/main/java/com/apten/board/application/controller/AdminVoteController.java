package com.apten.board.application.controller;

import com.apten.board.application.model.request.VotePatchReq;
import com.apten.board.application.model.request.VotePostReq;
import com.apten.board.application.model.response.VoteDeleteRes;
import com.apten.board.application.model.response.VotePatchRes;
import com.apten.board.application.model.response.VotePostRes;
import com.apten.board.application.service.VoteService;
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

// 관리자 투표 관리 API 진입점
// 투표 등록과 수정, 삭제 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/votes")
public class AdminVoteController {

    // 투표 응용 서비스
    private final VoteService voteService;

    // 투표 등록 API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VotePostRes> createVote(@RequestBody VotePostReq request) {
        return ResultResponse.success("투표 등록 성공", voteService.createVote(request));
    }

    // 투표 수정 API
    @PatchMapping("/{voteUid}")
    public ResultResponse<VotePatchRes> updateVote(
            @PathVariable String voteUid,
            @RequestBody VotePatchReq request
    ) {
        return ResultResponse.success("투표 수정 성공", voteService.updateVote(voteUid, request));
    }

    // 투표 삭제 API
    @DeleteMapping("/{voteUid}")
    public ResultResponse<VoteDeleteRes> deleteVote(@PathVariable String voteUid) {
        return ResultResponse.success("투표 삭제 성공", voteService.deleteVote(voteUid));
    }
}
