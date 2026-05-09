package com.apten.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// relay가 전송 대기 outbox 이벤트를 제한적으로 조회하고 상태를 변경하는 repository이다.
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    // 아직 전송되지 않은 이벤트만 오래된 순서로 최대 100건 조회한다.
    List<Outbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
