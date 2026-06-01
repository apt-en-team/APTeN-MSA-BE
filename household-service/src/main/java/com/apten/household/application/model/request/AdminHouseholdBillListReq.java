package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdBillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 관리비 목록 조회 요청 DTO이다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminHouseholdBillListReq {

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 청구 상태 문자열이다. (DRAFT, CONFIRMED 또는 display value)
    private String status;

    // status 문자열을 HouseholdBillStatus enum으로 변환한다.
    public HouseholdBillStatus resolveStatus() {
        if (status == null || status.isBlank()) return null;
        try {
            return HouseholdBillStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            for (HouseholdBillStatus s : HouseholdBillStatus.values()) {
                if (s.getValue().equals(status)) return s;
            }
            return null;
        }
    }

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
