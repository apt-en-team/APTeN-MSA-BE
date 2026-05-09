package com.apten.facilityreservation.infrastructure.redis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

// 좌석 TEMP_HOLD Redis 접근 뼈대를 담당하는 어댑터이다.
@Component
@RequiredArgsConstructor
public class ReservationTempHoldRedisService {

    // String 기반 hold key/value 저장에 사용한다.
    private final StringRedisTemplate stringRedisTemplate;

    // 좌석 TEMP_HOLD key를 생성한다.
    public String buildHoldKey(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return ReservationTempHoldRedisKeys.buildSeatHoldKey(facilityId, seatId, reservationDate, startTime, endTime);
    }

    // 좌석 선점을 시도한다.
    public boolean tryHoldSeat(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            Long userId,
            Duration ttl
    ) {
        String holdKey = buildHoldKey(facilityId, seatId, reservationDate, startTime, endTime);

        // TODO 2단계에서 setIfAbsent(value, ttl)로 실제 선점을 구현한다.
        // TODO value에는 holdId 또는 userId 등 추적 가능한 최소 값을 저장할지 결정한다.
        // TODO Redis 장애 시 DB 기반 fallback 전략이 필요한지 검토한다.
        return false;
    }

    // 선점 key 존재 여부를 조회한다.
    public boolean existsHold(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        String holdKey = buildHoldKey(facilityId, seatId, reservationDate, startTime, endTime);

        // TODO 2단계에서 hasKey 또는 value 존재 여부 검증을 구현한다.
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(holdKey));
    }

    // 예약 확정 전에 hold key 유효성을 검증한다.
    public boolean validateHold(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            Long userId
    ) {
        String holdKey = buildHoldKey(facilityId, seatId, reservationDate, startTime, endTime);

        // TODO 2단계에서 Redis value와 userId 또는 holdId를 비교해 실제 유효성을 검증한다.
        // TODO TTL 만료 직전 경쟁 상황에서 DB hold 상태와 Redis key를 함께 확인하는 흐름을 구현한다.
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(holdKey));
    }

    // 예약 취소 또는 확정 후 hold key를 해제한다.
    public void releaseHold(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        String holdKey = buildHoldKey(facilityId, seatId, reservationDate, startTime, endTime);

        // TODO 2단계에서 예약 확정/취소 후 Redis key 삭제를 구현한다.
        stringRedisTemplate.delete(holdKey);
    }
}
