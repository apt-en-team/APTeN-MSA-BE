package com.apten.board.domain.entity;

import com.apten.board.domain.enums.VoteChoice;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 참여 이력 엔티티이다.
@Entity
@Table(
        name = "vote_participation",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vote_household", columnNames = {"vote_id", "household_id"})
        },
        indexes = {
                @Index(name = "idx_vote_participation_user_id", columnList = "user_id"),
                @Index(name = "idx_vote_participation_choice", columnList = "choice"),
                @Index(name = "idx_vote_participation_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteParticipation extends BaseEntity {

    // 참여 ID이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 투표 ID이다.
    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    // 세대 ID이다.
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 사용자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 선택값이다.
    @Column(name = "choice", nullable = false, length = 20)
    private VoteChoice choice;
}
