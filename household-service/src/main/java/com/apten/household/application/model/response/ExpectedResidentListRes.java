package com.apten.household.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 관리자 명부 목록 응답 DTO이다.
@Getter
@Builder
public class ExpectedResidentListRes {

    private List<ExpectedResidentRes> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}
