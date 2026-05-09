package com.apten.apartmentcomplex.infrastructure.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Auth Service 내부 관리자 생성 요청 예시 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAdminCreateReq {

    private Long complexId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String adminRole;
}
