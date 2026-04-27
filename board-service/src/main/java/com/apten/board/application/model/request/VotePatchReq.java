package com.apten.board.application.model.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 수정 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotePatchReq {

    // 수정할 제목이다.
    private String title;

    // 수정할 설명이다.
    private String description;

    // 수정할 시작 시각이다.
    private LocalDateTime startAt;

    // 수정할 종료 시각이다.
    private LocalDateTime endAt;
}
