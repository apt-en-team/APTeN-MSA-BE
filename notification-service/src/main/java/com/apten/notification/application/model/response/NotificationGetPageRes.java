package com.apten.notification.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 알림 목록 페이지 응답 DTO
@Getter
@Builder
public class NotificationGetPageRes {
    private PageResponse<NotificationRes> page;

    public static NotificationGetPageRes empty(int page, int size) {
        return NotificationGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
