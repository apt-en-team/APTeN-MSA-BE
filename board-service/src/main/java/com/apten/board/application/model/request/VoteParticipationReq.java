package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 참여 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteParticipationReq {

    // 참여 선택값이다.
    private String choice;
}
