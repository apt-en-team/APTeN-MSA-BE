package com.apten.common.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// producer 서비스에서 저장된 outbox 이벤트를 Kafka로 보내고 성공한 row를 삭제하는 relay이다.
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final long STUCK_THRESHOLD_MINUTES = 2;

    // INIT 행을 PROCESSING으로 커밋한 뒤 Kafka로 전송한다.
    // @Scheduled + @Transactional은 스케줄러가 프록시를 통해 호출하므로 트랜잭션이 정상 동작한다.
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void publishEvents() {
        List<Outbox> events;
        try {
            events = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.INIT);
            events.forEach(e -> {
                e.markProcessing();
                outboxRepository.save(e);
            });
        } catch (DataAccessException exception) {
            log.warn("Outbox relay skipped because outbox storage is not ready yet.", exception);
            return;
        }
        // 트랜잭션 커밋 후 Kafka 전송 — 커밋 시점에 PROCESSING이 DB에 반영되어 중복 발행을 막는다.
        events.forEach(this::publishEvent);
    }

    // 앱 크래시 등으로 PROCESSING에 묶인 row를 INIT으로 되돌린다.
    // publishEvents()와 별도 @Scheduled로 분리해 자기 호출(self-invocation) 문제를 피한다.
    @Scheduled(fixedDelay = 120000)
    @Transactional
    public void recoverStuckRows() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(STUCK_THRESHOLD_MINUTES);
        List<Outbox> stuckRows = outboxRepository.findByStatusAndCreatedAtBefore(OutboxStatus.PROCESSING, threshold);
        if (!stuckRows.isEmpty()) {
            log.warn("Outbox relay: stuck PROCESSING row {} 건 복구.", stuckRows.size());
            stuckRows.forEach(row -> {
                row.markInit();
                outboxRepository.save(row);
            });
        }
    }

    private void publishEvent(Outbox outbox) {
        String key = String.valueOf(outbox.getAggregateId());
        kafkaTemplate.send(outbox.getTopic(), key, outbox.getPayload())
                .whenComplete((result, exception) -> handlePublishResult(outbox, exception));
    }

    private void handlePublishResult(Outbox outbox, Throwable exception) {
        if (exception == null) {
            outboxRepository.delete(outbox);
            log.info("Outbox event published. outboxId={}, topic={}, eventType={}",
                    outbox.getId(), outbox.getTopic(), outbox.getEventType());
            return;
        }
        outbox.markFailed();
        outboxRepository.save(outbox);
        log.error("Outbox event publish failed. outboxId={}, topic={}, eventType={}",
                outbox.getId(), outbox.getTopic(), outbox.getEventType(), exception);
    }
}
