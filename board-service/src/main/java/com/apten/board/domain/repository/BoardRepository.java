package com.apten.board.domain.repository;

import com.apten.board.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 기본 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper의 MyBatis 매퍼로 분리하는 규칙을 따른다
public interface BoardRepository extends JpaRepository<Board, Long> {
}
