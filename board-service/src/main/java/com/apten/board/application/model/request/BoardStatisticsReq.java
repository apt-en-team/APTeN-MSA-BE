package com.apten.board.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시판 통계 조회 요청이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardStatisticsReq {

    // 조회 시작일이다.
    private LocalDate startDate;

    // 조회 종료일이다.
    private LocalDate endDate;
}
