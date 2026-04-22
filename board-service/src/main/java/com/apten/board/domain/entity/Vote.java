package com.apten.board.domain.entity;

import com.apten.board.domain.enums.VoteStatus;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공지 기반 투표 원본 엔티티
// 찬반 집계와 기간 정보를 이 테이블이 관리한다
@Entity
@Table(name = "vote")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends BaseEntity {

    // 투표 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 연결된 공지 ID
    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    // 투표 제목
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 투표 설명
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 시작 시각
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    // 종료 시각
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    // 투표 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VoteStatus status;

    // 찬성 수
    @Column(name = "agree_count", nullable = false)
    private Integer agreeCount;

    // 반대 수
    @Column(name = "disagree_count", nullable = false)
    private Integer disagreeCount;

    // 참여 세대 수
    @Column(name = "household_count", nullable = false)
    private Integer householdCount;

    // 투표 기본 정보를 수정할 때 사용한다
    public void update(String title, String description, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
