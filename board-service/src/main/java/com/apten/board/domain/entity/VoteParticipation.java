package com.apten.board.domain.entity;

import com.apten.board.domain.enums.VoteChoice;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 참여 이력 엔티티
// 세대당 1회 참여 제한은 vote_id와 household_id 유니크 기준으로 관리한다
@Entity
@Table(
        name = "vote_participation",
        uniqueConstraints = @UniqueConstraint(name = "uk_vote_household", columnNames = {"vote_id", "household_id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteParticipation extends BaseEntity {

    // 참여 이력 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 대상 투표 ID
    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    // 참여 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 참여 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 선택값
    @Enumerated(EnumType.STRING)
    @Column(name = "choice", nullable = false, length = 20)
    private VoteChoice choice;
}
