package com.apten.facilityreservation.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 목록 API에서 페이지 메타데이터를 함께 내려줄 공통 응답 모델
@Getter
@Builder
public class PageResponse<T> {

    // 현재 페이지 내용
    private final List<T> content;

    // 현재 페이지 번호
    private final int page;

    // 페이지 크기
    private final int size;

    // 전체 데이터 수
    private final long totalElements;

    // 전체 페이지 수
    private final int totalPages;

    // 다음 페이지 존재 여부
    private final boolean hasNext;

    // 로직 구현 전 단계에서 빈 페이지 응답을 빠르게 만든다
    public static <T> PageResponse<T> empty(int page, int size) {
        return PageResponse.<T>builder()
                .content(List.of())
                .page(Math.max(page, 0))
                .size(size > 0 ? size : 20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .build();
    }
}
