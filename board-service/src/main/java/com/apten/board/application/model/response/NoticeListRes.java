package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeListRes {
    private final Long noticeId;
    private final String title;
    private final String writerName;
    private final LocalDateTime createdAt;
    private final String thumbSavedName;
    private final Boolean hasFile;
}