package com.apten.board.domain.entity;

import com.apten.board.domain.enums.BoardStatus;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// board-service의 게시글 집합 루트를 대표하는 최소 엔티티
// 게시글 상세 규칙을 넣기 전에도 JPA 기준 엔티티 형태가 보이도록 최소 필드만 둔다
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {

    // 모든 서비스 엔티티가 같은 PK 규칙을 따르도록 TSID 마커를 함께 둔다
    @Id
    @Tsid
    private Long id;

    // 게시글 기본 제목 필드
    @Column(nullable = false)
    private String title;

    // 게시글 사용 상태를 문자열 enum으로 저장한다
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardStatus status;
}
