package com.apten.board.application.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

// board-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 게시글 작성, 수정, 댓글 요청 모델을 같은 패키지 규칙으로 확장하기 위한 기준 파일이다
@Getter
@NoArgsConstructor
public class BoardBaseRequest {
}
