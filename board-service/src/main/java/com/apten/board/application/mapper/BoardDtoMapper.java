package com.apten.board.application.mapper;

import com.apten.board.application.model.dto.BoardDto;
import com.apten.board.application.model.request.BoardBaseRequest;
import com.apten.board.application.model.response.BoardBaseResponse;
import com.apten.board.domain.entity.Board;
import org.springframework.stereotype.Component;

// board-service의 요청, 응답, 내부 DTO 변환을 맡는 전용 매퍼
// 서비스가 변환 코드까지 떠안지 않도록 application 계층 안에서 역할을 분리한다
@Component
public class BoardDtoMapper {

    // 요청 DTO를 저장 전 엔티티 형태로 옮긴다
    public Board toEntity(BoardBaseRequest request) {
        return Board.builder()
                .title(request.getTitle())
                .status(request.getStatus())
                .build();
    }

    // 엔티티를 서비스 내부 전달용 DTO로 바꾼다
    public BoardDto toDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .status(board.getStatus())
                .build();
    }

    // 내부 DTO를 외부 응답 모델로 바꾼다
    public BoardBaseResponse toResponse(BoardDto boardDto) {
        return BoardBaseResponse.builder()
                .id(boardDto.getId())
                .title(boardDto.getTitle())
                .status(boardDto.getStatus())
                .build();
    }
}
