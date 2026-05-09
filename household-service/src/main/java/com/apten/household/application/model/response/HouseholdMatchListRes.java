package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdMatchProcessType;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 수동 승인 대상 조회 응답 DTO이다.
@Getter
@Builder
public class HouseholdMatchListRes {

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

    // 매칭 요청 한 건이다.
    @Getter
    @Builder
    public static class Item {
        private Long matchRequestId;
        private Long userId;
        private Long complexId;
        private String inputName;
        private String inputPhone;
        private LocalDate inputBirthDate;
        private String inputBuilding;
        private String inputUnit;
        private Long matchedHouseholdId;
        private HouseholdMatchProcessType processType;
        private HouseholdMatchStatus matchStatus;
        private LocalDateTime processedAt;
        private LocalDateTime createdAt;
    }
}
