package com.apten.board.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 참여 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteParticipationPostReq {
    private String optionUid;
}
