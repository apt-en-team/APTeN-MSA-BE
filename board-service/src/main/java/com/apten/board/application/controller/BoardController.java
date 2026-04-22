package com.apten.board.application.controller;

import com.apten.board.application.model.response.BoardBaseResponse;
import com.apten.board.application.service.BoardService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// board-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 이후 게시글과 댓글 API는 이 계층 아래에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    // 게시판 응용 계층 진입점
    private final BoardService boardService;

    // 공통 응답 포맷과 기본 라우팅 연결이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<BoardBaseResponse> getBoardTemplate() {
        return ResultResponse.success("board template ready", boardService.getBoardTemplate());
    }
}
