package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdBillItemType;
import com.apten.household.domain.enums.HouseholdBillStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 관리자 관리비 상세 조회 응답 DTO이다.
@Getter
@Builder
public class AdminHouseholdBillDetailRes {

    // 청구 ID이다.
    private Long billId;

    // 세대 ID이다.
    private Long householdId;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 기본 관리비이다.
    private BigDecimal baseFee;

    // 차량 비용이다.
    private BigDecimal vehicleFee;

    // 시설 비용이다.
    private BigDecimal facilityFee;

    // 방문차량 비용이다.
    private BigDecimal visitorFee;

    // 총 비용이다.
    private BigDecimal totalFee;

    // 청구 상태이다.
    private HouseholdBillStatus status;

    // 청구 항목 목록이다.
    private List<Item> items;

    // 확정 시각이다.
    private LocalDateTime confirmedAt;

    // 청구 항목 한 건이다.
    @Getter
    @Builder
    public static class Item {
        private HouseholdBillItemType itemType;
        private String itemName;
        private BigDecimal amount;
        private String calcMemo;
    }
}
