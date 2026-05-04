package com.apten.auth.application.model.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 내 계정 정보 조회 응답
@Getter
@Builder
public class UserMeRes {

    // 회원 ID
    private Long userId;

    // 이메일
    private String email;

    // 이름
    private String name;

    // 휴대폰 번호
    private String phone;

    // 생년월일 — USER 전용, MANAGER/ADMIN은 null
    private LocalDate birthDate;

    // 동 정보 — USER 전용
    private String building;

    // 호 정보 — USER 전용
    private String unit;

    // 역할
    private String role;

    // 계정 상태
    private String status;

    // 가입 방식
    private String signupType;

    // 소속 단지 ID
    private Long complexId;

    // 계정 생성일
    private LocalDateTime createdAt;
}