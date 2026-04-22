package com.apten.household.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 목록 응답에서 페이지 정보를 함께 내려줄 공통 응답 모델
@Getter
@Builder
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    // 비즈니스 로직 전 단계에서 빈 페이지 응답을 빠르게 만들 때 사용한다
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
