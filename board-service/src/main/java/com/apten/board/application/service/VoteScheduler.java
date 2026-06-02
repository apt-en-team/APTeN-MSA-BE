package com.apten.board.application.service;

import com.apten.board.domain.entity.Vote;
import com.apten.board.domain.enums.VoteStatus;
import com.apten.board.domain.repository.VoteRepository;
import com.apten.board.infrastructure.kafka.BoardOutboxService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 투표 상태 자동 전환 스케줄러이다.
@Slf4j
@Service
@RequiredArgsConstructor
public class VoteScheduler {

    private final VoteRepository voteRepository;
    private final BoardOutboxService boardOutboxService;

    // 매 5분마다 기간이 지난 투표를 자동 종료한다.
    @Scheduled(fixedDelay = 300_000)
    @Transactional
    public void closeExpiredVotes() {
        LocalDateTime now = LocalDateTime.now();

        // 기간이 지났지만 아직 READY 또는 OPEN 상태인 투표를 조회한다.
        List<Vote> expiredVotes = voteRepository.findExpiredVotes(now, VoteStatus.READY, VoteStatus.OPEN);

        if (expiredVotes.isEmpty()) return;

        log.info("[VoteScheduler] 만료된 투표 {}건 자동 종료 처리", expiredVotes.size());

        for (Vote vote : expiredVotes) {
            vote.close();
            boardOutboxService.saveVoteClosedEvent(vote);
            log.info("[VoteScheduler] 투표 자동 종료 — voteId={}, title={}", vote.getId(), vote.getTitle());
        }
    }
}