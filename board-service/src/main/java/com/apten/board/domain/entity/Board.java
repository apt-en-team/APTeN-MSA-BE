package com.apten.board.domain.entity;

import com.apten.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// board-service의 게시글 집합 루트를 대표하는 최소 엔티티
// 모든 도메인 엔티티가 BaseEntity를 상속한다는 공통 규칙을 실제로 보여주는 시작 파일이다
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board extends BaseEntity {

    // 다음 단계에서 TSID 규칙으로 확장할 기본 식별자
    @Id
    private Long id;
}
