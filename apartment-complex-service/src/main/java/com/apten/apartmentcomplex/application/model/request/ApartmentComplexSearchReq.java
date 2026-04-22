package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 단지 목록 조회 요청 DTO
// 검색어와 페이지 정보를 함께 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexSearchReq {
    private String keyword;
    private Integer page;
    private Integer size;

    // 목록 조회 기본 페이지를 맞춘다
    public int getPage() {
        return page != null ? page : 0;
    }

    // 목록 조회 기본 크기를 맞춘다
    public int getSize() {
        return size != null ? size : 20;
    }
}
