package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 공지 삭제 응답이다.
@Getter
@Builder
public class NoticeDeleteRes {
    private final Long noticeId;
    private final LocalDateTime deletedAt;
}
