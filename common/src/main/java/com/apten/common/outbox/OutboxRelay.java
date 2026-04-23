package com.apten.common.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// producer 서비스에서 저장된 outbox 이벤트를 Kafka로 보내고 성공한 row를 삭제하는 relay이다.
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "key-serializer")
public class OutboxRelay {

    // INIT 상태 이벤트를 조회하고 성공/실패 결과를 저장하는 repository이다.
    private final OutboxRepository outboxRepository;

    // JSON 문자열 payload를 Kafka로 전송하는 producer 템플릿이다.
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 일정 주기로 INIT 이벤트를 최대 100건 읽어 Kafka 전송을 시도한다.
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void publishEvents() {
        // 오래된 INIT 이벤트부터 처리해서 서비스 이벤트 순서를 최대한 유지한다.
        List<Outbox> events = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.INIT);

        // 각 이벤트를 독립적으로 전송해서 한 건 실패가 다른 이벤트 전송을 막지 않게 한다.
        events.forEach(this::publishEvent);
    }

    // outbox 한 건을 Kafka로 보내고 성공하면 삭제, 실패하면 FAILED로 변경한다.
    private void publishEvent(Outbox outbox) {
        // aggregateId를 key로 사용해 같은 원본 데이터 이벤트가 같은 파티션으로 가도록 한다.
        String key = String.valueOf(outbox.getAggregateId());

        // payload는 이미 JSON 문자열이므로 변환하지 않고 그대로 Kafka에 전달한다.
        kafkaTemplate.send(outbox.getTopic(), key, outbox.getPayload())
                .whenComplete((result, exception) -> handlePublishResult(outbox, exception));
    }

    // Kafka 전송 결과에 따라 outbox row를 삭제하거나 FAILED 상태로 저장한다.
    private void handlePublishResult(Outbox outbox, Throwable exception) {
        if (exception == null) {
            // Kafka 전송 성공 시 같은 이벤트가 다시 발행되지 않도록 row를 삭제한다.
            outboxRepository.delete(outbox);
            log.info("Outbox event published. outboxId={}, topic={}, eventType={}",
                    outbox.getId(), outbox.getTopic(), outbox.getEventType());
            return;
        }

        // Kafka 전송 실패 시 재처리 기준을 남기기 위해 FAILED 상태로 변경한다.
        outbox.markFailed();
        outboxRepository.save(outbox);
        log.error("Outbox event publish failed. outboxId={}, topic={}, eventType={}",
                outbox.getId(), outbox.getTopic(), outbox.getEventType(), exception);
    }
}
