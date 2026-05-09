package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 목록 응답 아이템이다.
@Getter
@Builder
public class NoticeListRes {
    private final Long noticeId;
    private final String title;
    private final LocalDateTime createdAt;
}
