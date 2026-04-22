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

// 공지사항 원본 엔티티
// 관리자 공지 제목과 본문, 삭제 상태를 이 테이블이 관리한다
@Entity
@Table(name = "notice")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {

    // 공지 내부 PK
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

    // 공지 제목
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 공지 본문
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 소프트 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 삭제 시각
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 공지 내용을 수정할 때 사용한다
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 공지를 소프트 삭제할 때 사용한다
    public void markDeleted(LocalDateTime deletedAt) {
        this.isDeleted = true;
        this.deletedAt = deletedAt;
    }
}
