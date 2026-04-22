package com.apten.board.application.service;

import com.apten.board.application.mapper.BoardDtoMapper;
import com.apten.board.application.model.dto.BoardDto;
import com.apten.board.application.model.response.BoardBaseResponse;
import com.apten.board.domain.entity.Board;
import com.apten.board.domain.enums.BoardStatus;
import com.apten.board.domain.repository.BoardRepository;
import com.apten.board.infrastructure.mapper.BoardQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// board-service 유스케이스를 조합할 기본 서비스 클래스
// JPA와 MyBatis를 함께 사용하는 위치가 application/service라는 점을 구조로 보여준다
@Service
@RequiredArgsConstructor
public class BoardService {

    // 단건 저장과 기본 조회는 JPA Repository가 맡는다
    private final BoardRepository boardRepository;

    // 복잡 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<BoardQueryMapper> boardQueryMapperProvider;

    // 요청 DTO와 응답 DTO 변환은 전용 매퍼에 맡긴다
    private final BoardDtoMapper boardDtoMapper;

    // 컨트롤러가 바로 연결할 수 있는 최소 응답 형태를 반환한다
    public BoardBaseResponse getBoardTemplate() {
        Board board = Board.builder()
                .id(1L)
                .title("board-template")
                .status(BoardStatus.DRAFT)
                .build();

        BoardDto boardDto = boardDtoMapper.toDto(board);
        return boardDtoMapper.toResponse(boardDto);
    }
}
