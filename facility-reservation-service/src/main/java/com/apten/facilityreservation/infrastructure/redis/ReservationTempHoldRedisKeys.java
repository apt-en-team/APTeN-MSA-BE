package com.apten.facilityreservation.infrastructure.redis;

import java.time.LocalDate;
import java.time.LocalTime;

// 좌석 TEMP_HOLD 전용 Redis key 규칙을 한 곳에서 관리한다.
public final class ReservationTempHoldRedisKeys {

    private static final String HOLD_KEY_PREFIX = "reservation:hold";

    private ReservationTempHoldRedisKeys() {
    }

    // 좌석 선점 key를 생성한다.
    public static String buildSeatHoldKey(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        // TODO LocalDate/LocalTime 포맷을 공통 유틸로 분리할지 2단계에서 검토한다.
        // TODO facilityId가 전역 TSID여도 단지 분리 명확성을 위해 complexId를 key에 포함할지 검토한다.
        return HOLD_KEY_PREFIX
                + ":" + facilityId
                + ":" + seatId
                + ":" + reservationDate
                + ":" + startTime
                + ":" + endTime;
    }
}
