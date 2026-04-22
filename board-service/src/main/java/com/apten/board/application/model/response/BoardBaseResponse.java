package com.apten.board.application.model.response;

import com.apten.board.domain.enums.BoardStatus;
import lombok.Builder;
import lombok.Getter;

// board-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 게시판 전용 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class BoardBaseResponse {

    // 게시글 식별자
    private final Long id;

    // 목록과 상세 화면에서 공통으로 쓸 제목
    private final String title;

    // 현재 게시글 상태
    private final BoardStatus status;
}
