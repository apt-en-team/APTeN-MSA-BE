package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 내 계정 정보 수정 응답
@Getter
@Builder
public class UserPatchRes {

    private String name;
    private String phone;
    private String message;
}