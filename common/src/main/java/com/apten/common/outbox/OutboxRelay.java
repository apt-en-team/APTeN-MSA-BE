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

    // INIT/PROCESSING 상태 이벤트를 조회하고 결과를 저장하는 repository이다.
    private final OutboxRepository outboxRepository;

    // JSON 문자열 payload를 Kafka로 전송하는 producer 템플릿이다.
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 앱 재시작 등으로 PROCESSING에 묶인 row를 INIT으로 되돌릴 임계값 (분)
    private static final long STUCK_THRESHOLD_MINUTES = 2;

    // 일정 주기로 INIT 이벤트를 PROCESSING으로 먼저 커밋한 뒤 Kafka로 전송한다.
    // PROCESSING 선점으로 다음 주기에서 같은 row가 중복 발행되는 것을 막는다.
    @Scheduled(fixedDelay = 10000)
    public void publishEvents() {
        recoverStuckRows();

        List<Outbox> events;
        try {
            events = markBatchAsProcessing();
        } catch (DataAccessException exception) {
            // outbox 테이블이 아직 준비되지 않은 초기 기동 구간에는 relay를 건너뛰고 서비스는 계속 살린다.
            log.warn("Outbox relay skipped because outbox storage is not ready yet.", exception);
            return;
        }

        // 각 이벤트를 독립적으로 전송해서 한 건 실패가 다른 이벤트 전송을 막지 않게 한다.
        events.forEach(this::publishEvent);
    }

    // INIT 행을 PROCESSING으로 상태 변경 후 커밋한다. Kafka 전송 전에 커밋해야 중복 방지가 된다.
    @Transactional
    public List<Outbox> markBatchAsProcessing() {
        List<Outbox> events = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.INIT);
        events.forEach(e -> {
            e.markProcessing();
            outboxRepository.save(e);
        });
        return events;
    }

    // 앱 크래시 등으로 PROCESSING 상태에 오래 묶인 row를 INIT으로 되돌려 재처리 대상에 포함시킨다.
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
