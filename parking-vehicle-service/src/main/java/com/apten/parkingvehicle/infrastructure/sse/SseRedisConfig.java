package com.apten.parkingvehicle.infrastructure.sse;

import com.apten.parkingvehicle.infrastructure.redis.SensorChangeSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

// SSE Pub/Sub 채널 상수와 메시지 리스너 컨테이너 설정
@Configuration
public class SseRedisConfig {

    // 자리 점유 상태 변경 이벤트 채널 이름
    public static final String SPOT_CHANGED_CHANNEL = "parking:spot:changed";

    // Pub/Sub 구독자 등록과 채널 매핑을 위한 컨테이너 빈
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            SensorChangeSubscriber subscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, new ChannelTopic(SPOT_CHANGED_CHANNEL));
        return container;
    }
}
