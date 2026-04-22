package com.apten.board.application.model.request;

import com.apten.board.domain.enums.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// board-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 게시글 작성과 수정에서 공통으로 받을 값만 먼저 정리해 둔다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardBaseRequest {

    // 게시글 제목 입력값
    private String title;

    // 게시글 상태 입력값
    private BoardStatus status;
}
