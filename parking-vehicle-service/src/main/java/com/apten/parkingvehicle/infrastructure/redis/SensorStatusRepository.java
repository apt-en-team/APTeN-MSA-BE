package com.apten.parkingvehicle.infrastructure.redis;

import com.apten.parkingvehicle.domain.enums.SensorStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

// 주차 센서 점유 상태를 Redis에 저장하고 조회하는 저장소
@Repository
@RequiredArgsConstructor
public class SensorStatusRepository {

    // 센서 Hash 키 접두사
    private static final String SENSOR_KEY_PREFIX = "parking:sensor:";

    // zone 점유 카운터 키 형식
    private static final String ZONE_OCCUPIED_KEY_FORMAT = "parking:zone:%d:occupied";

    // zone 토글 직렬화 분산락 키 형식
    private static final String ZONE_LOCK_KEY_FORMAT = "parking:zone:%d:lock";

    // 등록 센서 코드 Set 키
    private static final String REGISTERED_SET_KEY = "parking:sensors:registered";

    // 현재값이 0 초과일 때만 카운터를 감소시키는 원자 스크립트 (음수 하한 0 보장)
    private static final RedisScript<Long> DECREMENT_IF_POSITIVE_SCRIPT = RedisScript.of(
            "local v = tonumber(redis.call('GET', KEYS[1]) or '0') "
                    + "if v > 0 then return redis.call('DECR', KEYS[1]) else return 0 end",
            Long.class);

    // 락 토큰이 일치할 때만 락 키를 삭제하는 원자 스크립트 (타 소유자 해제 방지)
    private static final RedisScript<Long> UNLOCK_SCRIPT = RedisScript.of(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end",
            Long.class);

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
        String now = LocalDateTime.now().toString();

        // MULTI 시작 전 현재 Hash의 status 필드 조회로 차분 적용 방향 결정
        Map<String, String> currentHash = getSensorHash(sensorCode);
        String currentStatusRaw = currentHash.get(FIELD_STATUS);
        SensorStatus currentStatus = currentStatusRaw == null ? null : SensorStatus.valueOf(currentStatusRaw);

        // 센서 Hash 필드와 등록 Set을 한 트랜잭션으로 초기화 (JDK 직렬화 Hash 유지, 카운터는 분리 처리)
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
                operations.opsForHash().put(sensorKey, FIELD_ZONE_TOTAL_SLOTS, String.valueOf(zoneTotalSlots != null ? zoneTotalSlots : 0));
                operations.opsForSet().add(REGISTERED_SET_KEY, sensorCode);
                return operations.exec();
            }
        });

        // 현재 상태와 요청 상태 조합으로 zone 카운터 차분 적용 (감소는 floored Lua로 0 하한 보장)
        boolean toOccupied = initialStatus == SensorStatus.OCCUPIED
                && (currentStatus == null || currentStatus == SensorStatus.VACANT);
        boolean toVacant = currentStatus == SensorStatus.OCCUPIED && initialStatus == SensorStatus.VACANT;
        if (toOccupied) {
            redisTemplate.opsForValue().increment(buildZoneOccupiedKey(zoneId));
        } else if (toVacant) {
            decrementZoneOccupiedFloored(zoneId);
        }
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

    // 센서 상태 갱신 (실제 전이 발생 시 true, 동일 상태로 변화 없으면 false)
    public boolean updateStatus(String sensorCode, SensorStatus newStatus, boolean affectCounter) {
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
            return false;
        }

        String zoneIdRaw = hash.get(FIELD_ZONE_ID);
        if (zoneIdRaw == null) {
            throw new IllegalStateException("zone_id 필드 누락 센서: " + sensorCode);
        }
        Long zoneId = Long.valueOf(zoneIdRaw);
        String now = LocalDateTime.now().toString();

        // 상태와 변경 시각을 함께 갱신 (JDK 직렬화 Hash 유지, 카운터는 분리 처리)
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Object execute(RedisOperations operations) {
                operations.multi();
                operations.opsForHash().put(sensorKey, FIELD_STATUS, newStatus.name());
                operations.opsForHash().put(sensorKey, FIELD_CHANGED_AT, now);
                return operations.exec();
            }
        });

        // 활성 자리에 한해 전환 방향대로 zone 카운터 증감 (감소는 floored Lua로 0 하한 보장, 사용불가 자리는 제외)
        if (affectCounter) {
            if (newStatus == SensorStatus.OCCUPIED) {
                redisTemplate.opsForValue().increment(buildZoneOccupiedKey(zoneId));
            } else {
                decrementZoneOccupiedFloored(zoneId);
            }
        }
        return true;
    }

    // zone 점유 카운터 조회 — 음수 방지
    public Long getZoneOccupied(Long zoneId) {
        String raw = redisTemplate.opsForValue().get(buildZoneOccupiedKey(zoneId));
        if (raw == null) {
            return 0L;
        }
        return Math.max(0L, Long.valueOf(raw));
    }

    // zone 점유 카운터 +1 (자리 활성화 보정용)
    public void incrementZoneOccupied(Long zoneId) {
        redisTemplate.opsForValue().increment(buildZoneOccupiedKey(zoneId));
    }

    // zone 점유 카운터 -1 (자리 비활성화 보정용)
    public void decrementZoneOccupied(Long zoneId) {
        String key = buildZoneOccupiedKey(zoneId);
        String raw = redisTemplate.opsForValue().get(key);
        // 현재 값이 0 이하면 decrement 건너뜀 — Redis 카운터 음수 누적 방지
        if (raw != null && Long.parseLong(raw) <= 0) {
            return;
        }
        redisTemplate.opsForValue().decrement(key);
    }

    // zone 점유 카운터 floored 감소 (현재값 0 초과일 때만 원자 DECR로 음수 차단)
    public void decrementZoneOccupiedFloored(Long zoneId) {
        redisTemplate.execute(DECREMENT_IF_POSITIVE_SCRIPT, List.of(buildZoneOccupiedKey(zoneId)));
    }

    // zone 점유 카운터 절대값 보정 (재동기화로 원본 기준값 덮어쓰기, 음수 입력은 0으로 하한)
    public void setZoneOccupied(Long zoneId, long count) {
        long safe = Math.max(0L, count);
        redisTemplate.opsForValue().set(buildZoneOccupiedKey(zoneId), String.valueOf(safe));
    }

    // zone 토글 직렬화용 분산락 획득 시도 (획득 성공 시 true)
    public boolean tryLockZone(Long zoneId, String token, long ttlMs) {
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(buildZoneLockKey(zoneId), token, Duration.ofMillis(ttlMs));
        return Boolean.TRUE.equals(acquired);
    }

    // zone 분산락 해제 (자신이 획득한 토큰일 때만 원자 삭제)
    public void unlockZone(Long zoneId, String token) {
        redisTemplate.execute(UNLOCK_SCRIPT, List.of(buildZoneLockKey(zoneId)), token);
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
            // 분산 카운터 일관성이 깨진 경우에도 음수가 새어 나가지 않도록 0 하한 적용 (단건 getZoneOccupied와 동작 일치)
            result.put(zoneIds.get(i), raw == null ? 0L : Math.max(0L, Long.valueOf(raw)));
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

    // zone 분산락 키 조합
    private String buildZoneLockKey(Long zoneId) {
        return String.format(ZONE_LOCK_KEY_FORMAT, zoneId);
    }
}
