package com.apten.auth.application.model.request;

import com.apten.auth.domain.enums.SocialProvider;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 소셜 회원가입 추가 정보 입력 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSocialSignupPostReq {
    // 소셜 제공자
    private SocialProvider provider;
    // 제공자 사용자 ID
    private String providerUserId;
    // 선택한 단지 UID
    private String apartmentComplexUid;
    // 이메일
    private String email;
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
