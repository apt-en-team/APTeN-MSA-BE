package com.apten.common.outbox;

// Kafka 전송 대기 이벤트가 relay에서 어떤 상태인지 구분하는 enum이다.
public enum OutboxStatus {

    // 아직 Kafka로 보내지 않은 최초 저장 상태이다.
    INIT,

    // relay가 Kafka 전송을 시작한 상태이다. 중복 발행을 막기 위해 INIT 조회에서 제외된다.
    PROCESSING,

    // Kafka 전송 시도 중 실패해서 운영자가 재처리 기준으로 확인할 상태이다.
    FAILED
}
