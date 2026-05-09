package com.apten.auth.application.model.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 계정 정보 수정 요청
@Getter
@NoArgsConstructor
public class UserPatchReq {

    // 변경할 이름 — null이면 수정 안 함
    private String name;

    // 변경할 전화번호 — null이면 수정 안 함
    @Pattern(regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;
}