package com.apten.board.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 공지 목록 페이지 응답 DTO
@Getter
@Builder
public class NoticeGetPageRes {
    private final List<NoticeGetRes> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    public static NoticeGetPageRes empty(int page, int size) {
        return NoticeGetPageRes.builder()
                .content(List.of())
                .page(Math.max(page, 0))
                .size(size > 0 ? size : 20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .build();
    }
}
