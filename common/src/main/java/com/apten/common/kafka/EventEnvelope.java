package com.apten.common.kafka;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 모든 Kafka 이벤트가 같은 바깥 구조를 따르도록 맞추는 공통 envelope
// producer와 consumer가 payload 바깥 메타데이터를 같은 규칙으로 읽을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {

    // 이벤트 자체를 구분하는 고유 ID
    private String eventId;

    // 어떤 도메인 이벤트인지 나타내는 타입
    private EventType eventType;

    // 이벤트 스키마 버전
    private int version;

    // 원본 데이터 변경이 발생한 시각
    private Instant occurredAt;

    // 이벤트를 발행한 서비스명
    private String producer;

    // 캐시 테이블을 채우는 데 필요한 최소 데이터
    private T payload;
}
