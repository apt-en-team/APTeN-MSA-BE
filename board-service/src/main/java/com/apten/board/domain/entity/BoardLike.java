package com.apten.board.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 좋아요 엔티티
// 동일 사용자는 같은 게시글에 한 번만 좋아요할 수 있다
@Entity
@Table(
        name = "board_like",
        uniqueConstraints = @UniqueConstraint(name = "uk_board_like_post_user", columnNames = {"post_id", "user_id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardLike extends BaseEntity {

    // 좋아요 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 좋아요 대상 게시글 ID
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 좋아요 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
