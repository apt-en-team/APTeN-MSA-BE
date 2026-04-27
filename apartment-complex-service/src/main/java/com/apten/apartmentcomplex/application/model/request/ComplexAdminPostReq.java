package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 단지 소속 지정 요청 DTO이다.
// 특정 ADMIN 사용자를 단지에 배정할 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexAdminPostReq {

    // 배정할 관리자 사용자 ID이다.
    private Long userId;
}
