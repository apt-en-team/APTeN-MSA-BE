package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 세대 목록 조회 응답 DTO이다.
@Getter
@Builder
public class HouseholdListRes {

    // 목록 데이터이다.
    private List<Item> content;

    // 현재 페이지이다.
    private Integer page;

    // 페이지 크기이다.
    private Integer size;

    // 전체 건수이다.
    private Long totalElements;

    // 전체 페이지 수이다.
    private Integer totalPages;

    // 다음 페이지 존재 여부이다.
    private Boolean hasNext;

    // 세대 목록 한 건이다.
    @Getter
    @Builder
    public static class Item {
        private Long householdId;
        private Long complexId;
        private String building;
        private String unit;
        private HouseholdStatus status;
        private LocalDateTime createdAt;
    }
}
