package com.apten.auth.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이메일 회원가입 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegisterPostReq {
    // 선택한 단지 UID
    private String apartmentComplexUid;
    // 이메일
    private String email;
    // 비밀번호
    private String password;
    // 이름
    private String name;
    // 연락처
    private String phone;
    // 생년월일
    private LocalDate birthDate;
    // 동 정보
    private String dong;
    // 호 정보
    private String ho;
    // SMS 인증 코드
    private String authCode;
}
