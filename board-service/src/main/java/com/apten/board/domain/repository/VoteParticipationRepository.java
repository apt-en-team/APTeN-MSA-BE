package com.apten.board.domain.repository;

import com.apten.board.domain.entity.VoteParticipation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 투표 참여 저장소이다.
public interface VoteParticipationRepository extends JpaRepository<VoteParticipation, Long> {

    // 세대당 중복 참여 여부를 확인한다.
    boolean existsByVoteIdAndHouseholdId(Long voteId, Long householdId);

    // 결과 상세 조회에 사용한다.
    List<VoteParticipation> findByVoteId(Long voteId);
}
