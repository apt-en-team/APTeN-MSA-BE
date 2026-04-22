package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// GX 승인 대기 목록 페이지 응답 DTO
@Getter
@Builder
public class GxWaitingPageRes {
    private PageResponse<GxWaitingRes> page;

    public static GxWaitingPageRes empty(int page, int size) {
        return GxWaitingPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
