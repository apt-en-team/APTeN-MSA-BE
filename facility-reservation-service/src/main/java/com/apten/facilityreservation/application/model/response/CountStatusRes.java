package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 정원형 이용 현황 조회 응답 DTO이다.
@Getter
@Builder
public class CountStatusRes {

    // 시설 ID이다.
    private Long facilityId;

    // 조회 기준일이다.
    private LocalDate targetDate;

    // 최대 정원이다.
    private Integer maxCount;

    // 예약 인원이다.
    private Integer reservedCount;

    // 남은 정원이다.
    private Integer availableCount;

    // 예약자 목록이다.
    private List<UserItem> users;

    @Getter
    @Builder
    public static class UserItem {
        // TSID 정밀도 보호를 위해 문자열로 내려간다.
        private String reservationId;
        private String residentName;
        // HouseholdCache 기반. 캐시 미동기 시 null 가능
        private String dong;
        private String ho;
        private String unit;
        // 예약 상태 코드이다. 예: CONFIRMED, COMPLETED
        private String status;
    }
}
