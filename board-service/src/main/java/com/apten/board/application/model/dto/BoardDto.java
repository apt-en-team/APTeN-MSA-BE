package com.apten.board.application.model.dto;

import lombok.Builder;
import lombok.Getter;

// board-service 내부 계층 간 전달에 사용할 최소 DTO
// 컨트롤러 응답 모델과 엔티티 사이를 느슨하게 연결하는 중간 전달 객체다
@Getter
@Builder
public class BoardDto {
}
