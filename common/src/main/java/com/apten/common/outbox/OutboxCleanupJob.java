package com.apten.common.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// CDC 방식에서 Debezium이 row를 삭제하지 않으므로 보관 기간 초과 outbox row를 주기 정리한다.
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "apten.outbox", name = "cleanup-enabled", havingValue = "true")
public class OutboxCleanupJob {

    private final OutboxRepository outboxRepository;

    // 보관 기간(일). 기본값 3일.
    @Value("${apten.outbox.cleanup-retention-days:3}")
    private int retentionDays;

    // 실행 주기 cron. 기본값 매일 새벽 3시.
    @Scheduled(cron = "${apten.outbox.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void cleanup() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);
        int deleted = outboxRepository.deleteByCreatedAtBefore(threshold);
        log.info("Outbox cleanup 완료. {}건 삭제 (보관 기준: {}일 이전)", deleted, retentionDays);
    }
}
