package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

//추가 관리자 수정 응답 DTO
@Getter
@Builder
public class AdminPatchRes {

    private Long userId;
    private String email;
    private String name;
    private String phone;
    private String role;
    private Long complexId;
    private String status;
}
