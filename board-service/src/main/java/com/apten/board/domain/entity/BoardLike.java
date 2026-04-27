package com.apten.board.domain.entity;

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

// 게시글 좋아요 엔티티이다.
@Entity
@Table(
        name = "board_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_board_like_post_user", columnNames = {"post_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_board_like_user_id", columnList = "user_id"),
                @Index(name = "idx_board_like_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardLike extends BaseEntity {

    // 좋아요 ID이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 게시글 ID이다.
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 사용자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
