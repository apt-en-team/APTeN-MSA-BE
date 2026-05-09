package com.apten.auth.application.model.request;

import lombok.Getter;

//추가 단지 서비스 내부 연동용 관리자 수정 요청 DTO
@Getter
public class InternalAdminPatchReq {

    private String name;
    private String phone;
    private String adminRole;
    private String status;
}
