package com.apten.board.domain.entity;

import com.apten.board.domain.enums.VoteStatus;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공지 기반 투표 원본 엔티티이다.
@Entity
@Table(
        name = "vote",
        indexes = {
                @Index(name = "idx_vote_complex_id", columnList = "complex_id"),
                @Index(name = "idx_vote_notice_id", columnList = "notice_id"),
                @Index(name = "idx_vote_status", columnList = "status"),
                @Index(name = "idx_vote_start_end", columnList = "start_at,end_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends BaseEntity {

    // 투표 ID이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 공지 ID이다.
    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    // 제목이다.
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 설명이다.
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 시작 시각이다.
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    // 종료 시각이다.
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    // 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private VoteStatus status = VoteStatus.READY;

    // 찬성 수이다.
    @Builder.Default
    @Column(name = "agree_count", nullable = false)
    private Integer agreeCount = 0;

    // 반대 수이다.
    @Builder.Default
    @Column(name = "disagree_count", nullable = false)
    private Integer disagreeCount = 0;

    // 참여 세대 수이다.
    @Builder.Default
    @Column(name = "household_count", nullable = false)
    private Integer householdCount = 0;

    // 투표 기본 정보를 수정한다.
    public void update(String title, String description, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // 투표를 종료 상태로 변경한다.
    public void close() {
        this.status = VoteStatus.CLOSED;
    }

    // 투표를 진행 상태로 변경한다.
    public void open() {
        this.status = VoteStatus.OPEN;
    }

    // 찬성 집계를 올린다.
    public void increaseAgree() {
        this.agreeCount = this.agreeCount + 1;
        this.householdCount = this.householdCount + 1;
    }

    // 반대 집계를 올린다.
    public void increaseDisagree() {
        this.disagreeCount = this.disagreeCount + 1;
        this.householdCount = this.householdCount + 1;
    }
}
