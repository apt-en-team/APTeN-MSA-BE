package com.apten.board.infrastructure.mapper;

import com.apten.board.application.model.dto.BoardDto;
import java.util.List;

// board-service의 복잡 조회를 MyBatis로 분리할 때 사용할 Mapper 인터페이스
// 목록성 조회나 다중 조인 조회가 필요해지면 이 인터페이스와 XML을 함께 확장한다
public interface BoardQueryMapper {

    // 게시글 목록 조회가 필요해질 때 확장할 기본 메서드 위치
    List<BoardDto> findBoardSummaries();
}
