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

// 자유게시판 게시글 원본 엔티티
// 좋아요 수와 조회수를 포함한 게시글 본문을 이 테이블이 관리한다
@Entity
@Table(name = "board_post")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPost extends BaseEntity {

    // 게시글 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 작성자 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 게시글 제목
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 게시글 본문
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 조회수
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    // 좋아요 수
    // 좋아요 토글 결과는 이 필드 기준으로 관리한다
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    // 소프트 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 삭제 시각
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 게시글 내용을 수정할 때 사용한다
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 소프트 삭제 상태를 반영할 때 사용한다
    public void markDeleted(LocalDateTime deletedAt) {
        this.isDeleted = true;
        this.deletedAt = deletedAt;
    }
}
