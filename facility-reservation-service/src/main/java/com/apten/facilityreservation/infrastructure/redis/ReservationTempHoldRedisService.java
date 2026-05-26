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
        // Redis 선점은 동일 슬롯 동시 요청에서 가장 먼저 충돌을 막는 1차 방어선이다.
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(holdKey, String.valueOf(userId), ttl));
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
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(holdKey));
    }

    // 예약 확정 전에 hold key 유효성을 검증한다.
    // Redis value에 저장된 userId와 요청 userId가 일치해야 통과한다.
    // TTL 만료 시 key가 없으므로 자동으로 false를 반환한다.
    public boolean validateHold(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            Long userId
    ) {
        String holdKey = buildHoldKey(facilityId, seatId, reservationDate, startTime, endTime);
        String storedUserId = stringRedisTemplate.opsForValue().get(holdKey);
        return String.valueOf(userId).equals(storedUserId);
    }

    // 예약 확정 또는 취소 후 hold key를 해제한다.
    public void releaseHold(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        String holdKey = buildHoldKey(facilityId, seatId, reservationDate, startTime, endTime);
        stringRedisTemplate.delete(holdKey);
    }
}
