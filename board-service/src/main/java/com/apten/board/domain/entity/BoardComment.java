package com.apten.board.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 댓글 원본 엔티티
// 댓글 본문과 소프트 삭제 상태를 이 테이블이 관리한다
@Entity
@Table(name = "board_comment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardComment extends BaseEntity {

    // 댓글 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 연결된 게시글 ID
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 작성자 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 댓글 본문
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 소프트 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 삭제 시각
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 댓글 내용을 수정할 때 사용한다
    public void update(String content) {
        this.content = content;
    }

    // 댓글을 소프트 삭제할 때 사용한다
    public void markDeleted(LocalDateTime deletedAt) {
        this.isDeleted = true;
        this.deletedAt = deletedAt;
    }
}
