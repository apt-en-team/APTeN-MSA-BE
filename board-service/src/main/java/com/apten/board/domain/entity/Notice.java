package com.apten.board.domain.entity;

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

// 공지 원본 엔티티이다.
@Entity
@Table(
        name = "notice",
        indexes = {
                @Index(name = "idx_notice_complex_id", columnList = "complex_id"),
                @Index(name = "idx_notice_user_id", columnList = "user_id"),
                @Index(name = "idx_notice_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_notice_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {

    // 공지 ID이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 작성자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 제목이다.
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 본문이다.
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 삭제 여부이다.
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제 시각이다.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 공지 내용을 수정한다.
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 공지를 소프트 삭제한다.
    public void markDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
