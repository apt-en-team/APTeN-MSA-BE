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
        private Long reservationId;
        private String residentName;
    }
}
