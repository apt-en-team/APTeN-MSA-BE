package com.apten.board.domain.entity;

import com.apten.board.domain.enums.BoardFileType;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 첨부파일 메타데이터 엔티티이다.
@Entity
@Table(
        name = "board_file",
        indexes = {
                @Index(name = "idx_board_file_post_id", columnList = "post_id"),
                @Index(name = "idx_board_file_file_type", columnList = "file_type")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFile extends BaseEntity {

    // 파일 ID이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 게시글 ID이다.
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 원본 파일명이다.
    @Column(name = "origin_name", nullable = false, length = 255)
    private String originName;

    // 저장 파일명이다.
    @Column(name = "saved_name", nullable = false, length = 255)
    private String savedName;

    // 파일 경로이다.
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    // 파일 유형이다.
    @Column(name = "file_type", nullable = false, length = 20)
    private BoardFileType fileType;

    // 파일 크기이다.
    @Builder.Default
    @Column(name = "file_size", nullable = false)
    private Long fileSize = 0L;

    // 정렬 순서이다.
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
