package com.apten.common.security;

// 공통 사용자 역할이다.
// 플랫폼 전체 운영자와 단지 관리자, 일반 사용자 권한을 공통으로 맞춘다.
// MASTER → MANAGER → ADMIN → USER 순서로 권한 계층이 내려간다.
public enum UserRole {
    USER, // 입주민 - 본인이 직접 회원가입
    ADMIN, // 단지 관리자 - MANAGER가 계정 생성
    MANAGER, // 단지 관리 책임자 - MASTER가 계정 생성, ADMIN 계정 생성 권한 보유
    MASTER; // 플랫폼 전체 운영자 - MANAGER 계정 생성 권한 보유

    // code 값으로 enum 역조회 (JWT claim, DB 값 → enum 변환 시 사용)
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.name().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }

    // 관리자 계정 생성 권한 체크
    // MASTER → MANAGER 생성 가능
    // MANAGER → ADMIN 생성 가능
    public boolean canCreate(UserRole targetRole) {
        if (this == MASTER && targetRole == MANAGER) return true;
        if (this == MANAGER && targetRole == ADMIN) return true;
        return false;
    }
}