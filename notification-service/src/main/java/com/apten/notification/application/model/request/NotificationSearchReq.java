package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSearchReq {
    // 0부터 시작하는 페이지 번호
    private Integer page;
    // 한 페이지 크기
    private Integer size;
    // null이면 읽음/미읽음 전체를 조회한다
    private Boolean isRead;
    // null이면 모든 알림 유형을 조회한다
    private String type;
}
