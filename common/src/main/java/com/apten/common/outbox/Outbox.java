package com.apten.common.outbox;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 서비스 트랜잭션 안에서 Kafka 발행 대상 이벤트를 먼저 저장하는 공통 outbox 엔티티이다.
@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    // outbox 자체 PK이며 서비스 내부에서 TSID로 자동 생성된다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // relay가 이벤트를 보낼 Kafka 토픽명이다.
    @Column(name = "topic", nullable = false, length = 100)
    private String topic;

    // 원본 도메인 엔티티의 PK이며 Kafka key로 사용해 같은 aggregate 순서를 맞춘다.
    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;

    // consumer가 payload 의미를 구분할 이벤트 종류이다.
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    // Kafka에 그대로 전송할 JSON 문자열 payload이다.
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    // relay가 아직 보낼 이벤트인지 실패 이벤트인지 판단하는 상태값이다.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    // outbox row가 생성된 시각이며 오래된 이벤트부터 전송할 때 사용한다.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 서비스가 이벤트 저장을 요청할 때 INIT 상태와 생성 시각을 함께 세팅한다.
    @Builder
    private Outbox(String topic, Long aggregateId, String eventType, String payload) {
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxStatus.INIT;
        this.createdAt = LocalDateTime.now();
    }

    // Kafka 전송 실패 시 relay가 재조회 대상에서 제외되도록 FAILED로 변경한다.
    public void markFailed() {
        this.status = OutboxStatus.FAILED;
    }
}
