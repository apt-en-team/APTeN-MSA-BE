package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 삭제 응답 DTO
@Getter
@Builder
public class NoticeDeleteRes {
    private final String message;
    private final LocalDateTime deletedAt;
}
