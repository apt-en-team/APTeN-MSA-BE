package com.apten.notification.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 알림 목록 API에서 공통으로 쓰는 페이지 응답 모델
@Getter
@Builder
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

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
