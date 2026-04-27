package com.apten.board.infrastructure.kafka;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

// board-service 전용 outbox 이벤트 바깥 구조이다.
@Getter
@Builder
public class BoardEventEnvelope<T> {

    // 이벤트 ID이다.
    private final String eventId;

    // 이벤트 타입 문자열이다.
    private final String eventType;

    // 이벤트 스키마 버전이다.
    private final int version;

    // 이벤트 발생 시각이다.
    private final Instant occurredAt;

    // 이벤트 생산 서비스명이다.
    private final String producer;

    // 실제 payload이다.
    private final T payload;
}
