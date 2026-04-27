package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdBillStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 세대 비용 조회 응답 DTO이다.
@Getter
@Builder
public class MyBillListRes {

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

    // 세대 청구 한 건이다.
    @Getter
    @Builder
    public static class Item {
        private Long billId;
        private Integer billYear;
        private Integer billMonth;
        private BigDecimal totalFee;
        private HouseholdBillStatus status;
        private LocalDateTime confirmedAt;
        private LocalDateTime createdAt;
    }
}
