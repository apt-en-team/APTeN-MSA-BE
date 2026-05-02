package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 관리자 권한과 활성 여부 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexAdminPatchReq {

    private String adminRole;
    private Boolean isActive;
}
