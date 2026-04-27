package com.apten.board.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 목록 응답에서 페이지 정보를 함께 내려주는 공통 응답이다.
@Getter
@Builder
public class PageResponse<T> {

    // 현재 페이지 데이터이다.
    private final List<T> content;

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
}
