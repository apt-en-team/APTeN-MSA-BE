package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 승인 대기 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxWaitingSearchReq {
    private String gxProgramUid;
    private Integer page;
    private Integer size;

    public int getPage() {
        return page != null ? page : 0;
    }

    public int getSize() {
        return size != null ? size : 20;
    }
}
