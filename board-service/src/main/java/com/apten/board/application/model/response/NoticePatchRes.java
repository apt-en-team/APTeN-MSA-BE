package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 수정 응답이다.
@Getter
@Builder
public class NoticePatchRes {
    private final Long noticeId;
    private final String title;
    private final String content;
    private final LocalDateTime updatedAt;
}
