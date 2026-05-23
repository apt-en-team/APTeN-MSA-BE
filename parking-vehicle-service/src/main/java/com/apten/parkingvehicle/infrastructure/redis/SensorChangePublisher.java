package com.apten.parkingvehicle.infrastructure.redis;

import com.apten.parkingvehicle.application.model.event.ParkingSpotChangedEvent;
import com.apten.parkingvehicle.infrastructure.sse.SseRedisConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// 자리 점유 변경 이벤트를 Redis Pub/Sub 채널로 발행하는 컴포넌트
@Slf4j
@Component
@RequiredArgsConstructor
public class SensorChangePublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // 자리 변경 이벤트 발행
    public void publish(ParkingSpotChangedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(SseRedisConfig.SPOT_CHANGED_CHANNEL, json);
        } catch (JsonProcessingException e) {
            // Pub/Sub 휘발성 메시지이므로 직렬화 실패가 토글 자체를 막지 않게 한다.
            log.warn("SSE 변경 이벤트 직렬화 실패 sensorCode={}, complexId={}",
                    event.getSensorCode(), event.getComplexId(), e);
        }
    }
}
