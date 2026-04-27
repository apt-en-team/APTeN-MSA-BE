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

// 게시글 댓글 원본 엔티티이다.
@Entity
@Table(
        name = "board_comment",
        indexes = {
                @Index(name = "idx_board_comment_post_id", columnList = "post_id"),
                @Index(name = "idx_board_comment_user_id", columnList = "user_id"),
                @Index(name = "idx_board_comment_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_board_comment_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardComment extends BaseEntity {

    // 댓글 ID이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 게시글 ID이다.
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 작성자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 댓글 본문이다.
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 삭제 여부이다.
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제 시각이다.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 댓글 내용을 수정한다.
    public void update(String content) {
        this.content = content;
    }

    // 댓글을 소프트 삭제한다.
    public void markDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
