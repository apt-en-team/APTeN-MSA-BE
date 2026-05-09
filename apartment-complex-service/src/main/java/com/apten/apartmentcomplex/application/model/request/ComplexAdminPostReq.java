package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 계정을 생성하고 단지에 소속시키는 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexAdminPostReq {

    private String email;
    private String password;
    private String name;
    private String phone;
    private String adminRole;
}
