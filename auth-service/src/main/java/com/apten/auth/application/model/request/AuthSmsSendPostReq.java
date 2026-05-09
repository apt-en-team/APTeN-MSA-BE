package com.apten.auth.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// SMS 인증번호 발송 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSmsSendPostReq {
    // 이름
    private String name;
    // 연락처
    private String phone;
    // 생년월일
    private LocalDate birthDate;
}
