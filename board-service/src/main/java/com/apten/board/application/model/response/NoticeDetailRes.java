package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 상세 응답이다.
@Getter
@Builder
public class NoticeDetailRes {
    private final Long noticeId;
    private final Long complexId;
    private final Long userId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
