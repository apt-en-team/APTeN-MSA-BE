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

// 게시글 원본 엔티티이다.
@Entity
@Table(
        name = "board_post",
        indexes = {
                @Index(name = "idx_board_post_complex_id", columnList = "complex_id"),
                @Index(name = "idx_board_post_user_id", columnList = "user_id"),
                @Index(name = "idx_board_post_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_board_post_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPost extends BaseEntity {

    // 게시글 ID이다.
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

    // 조회수이다.
    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    // 좋아요 수이다.
    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    // 삭제 여부이다.
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제 시각이다.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 게시글 내용을 수정한다.
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 조회수를 증가시킨다.
    public void increaseViewCount() {
        this.viewCount = this.viewCount + 1;
    }

    // 좋아요 수를 증가시킨다.
    public void increaseLikeCount() {
        this.likeCount = this.likeCount + 1;
    }

    // 좋아요 수를 감소시킨다.
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    // 게시글을 소프트 삭제한다.
    public void markDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
