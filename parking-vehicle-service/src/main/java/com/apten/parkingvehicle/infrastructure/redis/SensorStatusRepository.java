package com.apten.parkingvehicle.infrastructure.redis;

import com.apten.parkingvehicle.domain.enums.SensorStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

// 주차 센서 점유 상태를 Redis에 저장하고 조회하는 저장소
@Repository
@RequiredArgsConstructor
public class SensorStatusRepository {

    // 센서 Hash 키 접두사
    private static final String SENSOR_KEY_PREFIX = "parking:sensor:";

    // zone 점유 카운터 키 형식
    private static final String ZONE_OCCUPIED_KEY_FORMAT = "parking:zone:%d:occupied";

    // 등록 센서 코드 Set 키
    private static final String REGISTERED_SET_KEY = "parking:sensors:registered";

    // 센서 Hash status 필드
    public static final String FIELD_STATUS = "status";

    // 센서 Hash changed_at 필드
    public static final String FIELD_CHANGED_AT = "changed_at";

    // 센서 Hash zone_id 필드
    public static final String FIELD_ZONE_ID = "zone_id";

    // 센서 Hash complex_id 필드
    public static final String FIELD_COMPLEX_ID = "complex_id";

    // 센서 Hash spot_number 필드
    public static final String FIELD_SPOT_NUMBER = "spot_number";

    // 센서 Hash zone_total_slots 필드
    public static final String FIELD_ZONE_TOTAL_SLOTS = "zone_total_slots";

    // 문자열 키/값 전용 RedisTemplate
    private final RedisTemplate<String, String> redisTemplate;

    // 센서 초기 상태 등록 (멱등 보장 — 같은 sensorCode 재호출 시 카운터와 Hash 최종 상태가 1회 호출과 동일)
    public void initSensor(String sensorCode, Long zoneId, Long complexId, String spotNumber, Integer zoneTotalSlots, SensorStatus initialStatus) {
        String sensorKey = buildSensorKey(sensorCode);
        String zoneKey = buildZoneOccupiedKey(zoneId);
        String now = LocalDateTime.now().toString();

        // MULTI 시작 전 현재 Hash의 status 필드 조회로 차분 적용 방향 결정
        Map<String, String> currentHash = getSensorHash(sensorCode);
        String currentStatusRaw = currentHash.get(FIELD_STATUS);
        SensorStatus currentStatus = currentStatusRaw == null ? null : SensorStatus.valueOf(currentStatusRaw);

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Object execute(RedisOperations operations) {
                operations.multi();
                operations.opsForHash().put(sensorKey, FIELD_STATUS, initialStatus.name());
                operations.opsForHash().put(sensorKey, FIELD_CHANGED_AT, now);
                operations.opsForHash().put(sensorKey, FIELD_ZONE_ID, String.valueOf(zoneId));
                operations.opsForHash().put(sensorKey, FIELD_COMPLEX_ID, String.valueOf(complexId));
                operations.opsForHash().put(sensorKey, FIELD_SPOT_NUMBER, spotNumber);
                operations.opsForHash().put(sensorKey, FIELD_ZONE_TOTAL_SLOTS, String.valueOf(zoneTotalSlots));
                operations.opsForSet().add(REGISTERED_SET_KEY, sensorCode);
                // 현재 상태와 요청 상태 조합으로 zone 카운터 차분 적용
                if (currentStatus == null && initialStatus == SensorStatus.OCCUPIED) {
                    operations.opsForValue().increment(zoneKey);
                } else if (currentStatus == SensorStatus.VACANT && initialStatus == SensorStatus.OCCUPIED) {
                    operations.opsForValue().increment(zoneKey);
                } else if (currentStatus == SensorStatus.OCCUPIED && initialStatus == SensorStatus.VACANT) {
                    operations.opsForValue().decrement(zoneKey);
                }
                return operations.exec();
            }
        });
    }

    // 센서 단일 상태 조회
    public SensorStatus getStatus(String sensorCode) {
        String raw = (String) redisTemplate.opsForHash().get(buildSensorKey(sensorCode), FIELD_STATUS);
        if (raw == null) {
            return null;
        }
        return SensorStatus.valueOf(raw);
    }

    // 센서 상태 일괄 조회. SessionCallback + pipeline으로 1 round trip 처리
    // RedisTemplate 직렬화기를 그대로 사용해 단건 조회와 byte 일관성 보장. status 없는 센서는 맵에서 제외
    public Map<String, SensorStatus> getStatusMap(List<String> sensorCodes) {
        if (sensorCodes == null || sensorCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object> rawResults = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Object execute(RedisOperations operations) {
                for (String sensorCode : sensorCodes) {
                    operations.opsForHash().get(buildSensorKey(sensorCode), FIELD_STATUS);
                }
                return null;
            }
        }, redisTemplate.getHashValueSerializer());

        Map<String, SensorStatus> result = new LinkedHashMap<>(sensorCodes.size());
        for (int i = 0; i < sensorCodes.size(); i++) {
            Object raw = rawResults.get(i);
            if (raw == null) {
                continue;
            }
            result.put(sensorCodes.get(i), SensorStatus.valueOf((String) raw));
        }
        return result;
    }

    // 센서 Hash 전체 조회
    public Map<String, String> getSensorHash(String sensorCode) {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(buildSensorKey(sensorCode));
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new LinkedHashMap<>(raw.size());
        raw.forEach((k, v) -> result.put(k.toString(), v == null ? null : v.toString()));
        return result;
    }

    // 센서 상태 갱신
    public void updateStatus(String sensorCode, SensorStatus newStatus) {
        String sensorKey = buildSensorKey(sensorCode);
        Map<String, String> hash = getSensorHash(sensorCode);
        if (hash.isEmpty()) {
            throw new IllegalStateException("초기화되지 않은 센서: " + sensorCode);
        }

        String currentStatusRaw = hash.get(FIELD_STATUS);
        if (currentStatusRaw == null) {
            throw new IllegalStateException("status 필드 누락 센서: " + sensorCode);
        }
        SensorStatus currentStatus = SensorStatus.valueOf(currentStatusRaw);
        if (currentStatus == newStatus) {
            return;
        }

        String zoneIdRaw = hash.get(FIELD_ZONE_ID);
        if (zoneIdRaw == null) {
            throw new IllegalStateException("zone_id 필드 누락 센서: " + sensorCode);
        }
        Long zoneId = Long.valueOf(zoneIdRaw);
        String zoneKey = buildZoneOccupiedKey(zoneId);
        String now = LocalDateTime.now().toString();

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Object execute(RedisOperations operations) {
                operations.multi();
                operations.opsForHash().put(sensorKey, FIELD_STATUS, newStatus.name());
                operations.opsForHash().put(sensorKey, FIELD_CHANGED_AT, now);
                // 전환 방향에 따라 zone 카운터 증감
                if (newStatus == SensorStatus.OCCUPIED) {
                    operations.opsForValue().increment(zoneKey);
                } else {
                    operations.opsForValue().decrement(zoneKey);
                }
                return operations.exec();
            }
        });
    }

    // zone 점유 카운터 조회
    public Long getZoneOccupied(Long zoneId) {
        String raw = redisTemplate.opsForValue().get(buildZoneOccupiedKey(zoneId));
        if (raw == null) {
            return 0L;
        }
        return Long.valueOf(raw);
    }

    // zone 카운터 일괄 조회. 키 없는 zone은 0L로 채워 반환
    public Map<Long, Long> getZoneOccupiedMap(List<Long> zoneIds) {
        if (zoneIds == null || zoneIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> keys = zoneIds.stream()
                .map(this::buildZoneOccupiedKey)
                .toList();
        List<String> rawValues = redisTemplate.opsForValue().multiGet(keys);
        Map<Long, Long> result = new LinkedHashMap<>(zoneIds.size());
        for (int i = 0; i < zoneIds.size(); i++) {
            String raw = (rawValues == null) ? null : rawValues.get(i);
            result.put(zoneIds.get(i), raw == null ? 0L : Long.valueOf(raw));
        }
        return result;
    }

    // 센서 Hash 존재 여부 확인
    public boolean exists(String sensorCode) {
        Boolean has = redisTemplate.hasKey(buildSensorKey(sensorCode));
        return Boolean.TRUE.equals(has);
    }

    // 등록 센서 중 무작위 1개 코드 조회
    public String getRandomSensorCode() {
        return redisTemplate.opsForSet().randomMember(REGISTERED_SET_KEY);
    }

    // 센서 Hash 키 조합
    private String buildSensorKey(String sensorCode) {
        return SENSOR_KEY_PREFIX + sensorCode;
    }

    // zone 카운터 키 조합
    private String buildZoneOccupiedKey(Long zoneId) {
        return String.format(ZONE_OCCUPIED_KEY_FORMAT, zoneId);
    }
}
