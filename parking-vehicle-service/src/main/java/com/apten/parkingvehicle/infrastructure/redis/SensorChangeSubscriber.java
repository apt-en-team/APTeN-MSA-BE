package com.apten.parkingvehicle.infrastructure.redis;

import com.apten.parkingvehicle.application.model.event.ParkingSpotChangedEvent;
import com.apten.parkingvehicle.application.service.SseSubscribeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

// 자리 점유 변경 이벤트를 Redis Pub/Sub로 수신해 SSE로 fan-out하는 컴포넌트
@Slf4j
@Component
@RequiredArgsConstructor
public class SensorChangeSubscriber implements MessageListener {

    private final SseSubscribeService sseSubscribeService;
    private final ObjectMapper objectMapper;

    // 채널 메시지 수신 처리
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            ParkingSpotChangedEvent event = objectMapper.readValue(json, ParkingSpotChangedEvent.class);
            if (event.getComplexId() == null) {
                // complexId 없는 페이로드는 라우팅 불가하므로 단일 메시지 단위로 차단한다.
                log.warn("SSE 변경 이벤트 complexId 누락 payload={}", json);
                return;
            }
            sseSubscribeService.publishToComplex(event.getComplexId(), event);
        } catch (IOException e) {
            // 역직렬화 실패가 다른 메시지 처리를 막지 않게 단일 메시지 단위로 차단한다.
            log.warn("SSE 변경 이벤트 수신 처리 실패 payload={}", json, e);
        }
    }
}
