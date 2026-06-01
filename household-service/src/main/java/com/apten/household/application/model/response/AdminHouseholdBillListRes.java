package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdBillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 관리자 관리비 목록 조회 응답 DTO이다.
@Getter
@Builder
public class AdminHouseholdBillListRes {

    // 목록 데이터이다.
    private List<Item> content;

    // 현재 페이지이다.
    private Integer page;

    // 페이지 크기이다.
    private Integer size;

    // 전체 건수이다.
    private Long totalElements;

    // 선택 기간 기준 전체 청구 건수이다.
    private Long totalBillCount;

    // 미확정 청구 건수이다.
    private Long draftCount;

    // 확정 청구 건수이다.
    private Long confirmedCount;

    // 전체 페이지 수이다.
    private Integer totalPages;

    // 다음 페이지 존재 여부이다.
    private Boolean hasNext;

    // 목록 한 건이다.
    @Getter
    @Builder
    public static class Item {
        private Long billId;
        private Long householdId;
        private String building;
        private String unit;
        private Integer billYear;
        private Integer billMonth;
        private BigDecimal baseFee;
        private BigDecimal vehicleFee;
        private BigDecimal facilityFee;
        private BigDecimal visitorFee;
        private BigDecimal totalFee;
        private BigDecimal lateFee;
        private BigDecimal payableAmount;
        private LocalDate sendDate;
        private LocalDate dueDate;
        private LocalDate overdueStartDate;
        private LocalDate homeDisplayUntil;
        private Boolean overdue;
        private HouseholdBillStatus status;
    }
}
