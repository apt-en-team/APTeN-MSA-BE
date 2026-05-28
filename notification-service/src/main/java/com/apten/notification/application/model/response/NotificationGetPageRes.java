package com.apten.notification.application.model.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

// 알림 목록 페이지 응답 DTO
@Getter
@Builder
public class NotificationGetPageRes {
    // 현재 페이지에 포함된 알림 목록
    private List<NotificationRes> content;
    // 0부터 시작하는 현재 페이지 번호
    private int page;
    // 요청 또는 기본 페이지 크기
    private int size;
    // 본인 알림 조건에 맞는 전체 건수
    private long totalElements;
    // 전체 페이지 수
    private int totalPages;
    // 다음 페이지 존재 여부
    private boolean hasNext;

    public static NotificationGetPageRes empty(int page, int size) {
        // 조회 결과가 없을 때도 프론트 페이지네이션 구조는 동일하게 유지한다
        return NotificationGetPageRes.builder()
                .content(List.of())
                .page(Math.max(page, 0))
                .size(size > 0 ? size : 20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .build();
    }
}
