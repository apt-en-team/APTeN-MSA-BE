package com.apten.board.domain.entity;

import com.apten.board.domain.enums.BoardFileType;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 첨부파일 엔티티
// 파일 저장 로직은 아직 없고 메타데이터 구조만 이 테이블이 가진다
@Entity
@Table(name = "board_file")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFile extends BaseEntity {

    // 첨부파일 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 연결된 게시글 ID
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 원본 파일명
    @Column(name = "origin_name", nullable = false, length = 255)
    private String originName;

    // 저장 파일명
    @Column(name = "saved_name", nullable = false, length = 255)
    private String savedName;

    // 파일 경로
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    // 파일 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private BoardFileType fileType;

    // 파일 크기
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    // 정렬 순서
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
