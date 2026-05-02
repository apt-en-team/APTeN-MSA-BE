package com.apten.apartmentcomplex.infrastructure.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Auth Service 내부 관리자 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAdminUpdateReq {

    private String adminRole;
    private String status;
}
