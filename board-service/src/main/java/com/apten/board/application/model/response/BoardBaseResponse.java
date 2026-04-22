package com.apten.board.application.model.response;

import lombok.Builder;
import lombok.Getter;

// board-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 게시판 전용 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class BoardBaseResponse {
}
