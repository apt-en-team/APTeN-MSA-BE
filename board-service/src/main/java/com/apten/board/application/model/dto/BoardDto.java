package com.apten.board.application.model.dto;

import com.apten.board.domain.enums.BoardStatus;
import lombok.Builder;
import lombok.Getter;

// board-service 내부 계층 간 전달에 사용할 최소 DTO
// 컨트롤러 응답 모델과 엔티티 사이를 느슨하게 연결하는 중간 전달 객체다
@Getter
@Builder
public class BoardDto {

    // 게시글 식별자
    private final Long id;

    // 게시글 제목
    private final String title;

    // 게시글 상태
    private final BoardStatus status;
}
