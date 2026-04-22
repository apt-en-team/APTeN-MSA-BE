package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 자유게시판 삭제 응답 DTO
@Getter
@Builder
public class FreeBoardDeleteRes {
    private final String message;
    private final LocalDateTime deletedAt;
}
