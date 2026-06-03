package com.apten.board.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 투표 목록 + 상태별 통계를 함께 내려주는 응답이다.
@Getter
@Builder
public class VotePageRes {

    // 현재 페이지 투표 목록이다.
    private final List<VoteListRes> content;

    // 현재 페이지 번호이다.
    private final int page;

    // 현재 페이지 크기이다.
    private final int size;

    // 전체 데이터 수이다.
    private final long totalElements;

    // 전체 페이지 수이다.
    private final int totalPages;

    // 다음 페이지 존재 여부이다.
    private final boolean hasNext;

    // 상태별 통계이다.
    private final Summary summary;

    // 상태별 투표 수 통계이다.
    @Getter
    @Builder
    public static class Summary {

        // 전체 투표 수이다.
        private final long total;

        // 진행 중 투표 수이다.
        private final long open;

        // 시작 전 투표 수이다.
        private final long ready;

        // 종료 투표 수이다.
        private final long closed;
    }
}