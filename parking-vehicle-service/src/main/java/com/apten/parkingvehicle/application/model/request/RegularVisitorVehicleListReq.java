package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 고정 방문차량 목록 조회 요청 DTO이다.
// 쿼리 파라미터를 @ModelAttribute로 바인딩하려면 세터가 필요하다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularVisitorVehicleListReq {

    // 활성 여부 필터이다.
    private Boolean isActive;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
