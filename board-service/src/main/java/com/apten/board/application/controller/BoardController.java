package com.apten.board.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// board-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 이후 게시글과 댓글 API는 이 계층 아래에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {
}
