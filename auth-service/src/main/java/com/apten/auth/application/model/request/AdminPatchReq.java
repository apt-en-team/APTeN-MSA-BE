package com.apten.auth.application.model.request;

import lombok.Getter;

//추가 관리자 이름, 연락처, 권한, 상태 수정 요청 DTO
@Getter
public class AdminPatchReq {

    private String name;
    private String phone;
    private String targetRole;
    private String status;
}
