package com.apten.board.application.model.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 생성 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteCreateReq {

    // 연결할 공지 ID이다.
    private Long noticeId;

    // 투표 제목이다.
    private String title;

    // 투표 설명이다.
    private String description;

    // 투표 시작 시각이다.
    private LocalDateTime startAt;

    // 투표 종료 시각이다.
    private LocalDateTime endAt;
}
