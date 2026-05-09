package com.apten.apartmentcomplex.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 관리자 단지 목록 페이지 응답 DTO
// 목록 데이터와 페이지 정보를 함께 내려줄 때 사용한다
@Getter
@Builder
public class ApartmentComplexGetPageRes {
    private final List<ApartmentComplexGetRes> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    // 비즈니스 로직 전 단계에서 빈 페이지 응답을 빠르게 만든다
    public static ApartmentComplexGetPageRes empty(int page, int size) {
        return ApartmentComplexGetPageRes.builder()
                .content(List.of())
                .page(Math.max(page, 0))
                .size(size > 0 ? size : 20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .build();
    }
}
