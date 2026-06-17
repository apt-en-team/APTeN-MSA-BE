package com.apten.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

// relay가 전송 대기 outbox 이벤트를 제한적으로 조회하고 상태를 변경하는 repository이다.
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    // 아직 전송되지 않은 이벤트만 오래된 순서로 최대 100건 조회한다.
    List<Outbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    // 앱 크래시 등으로 PROCESSING 상태에 묶인 행을 복구할 때 사용한다.
    List<Outbox> findByStatusAndCreatedAtBefore(OutboxStatus status, LocalDateTime threshold);

    // CDC 방식에서 Debezium이 발행 후 row를 자동 삭제하지 않으므로 보관 기간 초과분을 일괄 삭제한다.
    @Modifying
    @Query("DELETE FROM Outbox o WHERE o.createdAt < :threshold")
    int deleteByCreatedAtBefore(@Param("threshold") LocalDateTime threshold);
}
