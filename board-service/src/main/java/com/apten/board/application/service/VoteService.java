package com.apten.board.application.service;

import com.apten.board.application.model.request.VoteParticipationPostReq;
import com.apten.board.application.model.request.VotePatchReq;
import com.apten.board.application.model.request.VotePostReq;
import com.apten.board.application.model.request.VoteSearchReq;
import com.apten.board.application.model.response.VoteDeleteRes;
import com.apten.board.application.model.response.VoteGetDetailRes;
import com.apten.board.application.model.response.VoteGetPageRes;
import com.apten.board.application.model.response.VoteParticipationPostRes;
import com.apten.board.application.model.response.VotePatchRes;
import com.apten.board.application.model.response.VotePostRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 투표 응용 서비스
// 투표 조회와 등록, 수정, 삭제, 참여 시그니처를 이 서비스에 모아둔다
@Service
public class VoteService {

    // 투표 목록 조회 서비스
    public VoteGetPageRes getVoteList(VoteSearchReq request) {
        // TODO: 투표 목록 조회 로직 구현
        return VoteGetPageRes.empty(request.getPage(), request.getSize());
    }

    // 투표 상세 조회 서비스
    public VoteGetDetailRes getVoteDetail(String voteUid) {
        // TODO: 투표 상세 조회 로직 구현
        return VoteGetDetailRes.builder().build();
    }

    // 투표 등록 서비스
    public VotePostRes createVote(VotePostReq request) {
        // TODO: 투표 등록 로직 구현
        // TODO: 투표 등록 알림 이벤트 발행
        return VotePostRes.builder()
                .title(request.getTitle())
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 투표 수정 서비스
    public VotePatchRes updateVote(String voteUid, VotePatchReq request) {
        // TODO: 투표 수정 로직 구현
        return VotePatchRes.builder()
                .voteUid(voteUid)
                .title(request.getTitle())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 투표 삭제 서비스
    public VoteDeleteRes deleteVote(String voteUid) {
        // TODO: 투표 삭제 로직 구현
        return VoteDeleteRes.builder()
                .message("투표 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 투표 참여 서비스
    public VoteParticipationPostRes participateVote(String voteUid, VoteParticipationPostReq request) {
        // TODO: 세대주 여부 검증 후 투표 참여 처리
        // TODO: 세대당 1회 참여 제한 검증
        // TODO: 투표 기간 검증 처리
        return VoteParticipationPostRes.builder()
                .voteUid(voteUid)
                .optionUid(request.getOptionUid())
                .participatedAt(LocalDateTime.now())
                .build();
    }
}
