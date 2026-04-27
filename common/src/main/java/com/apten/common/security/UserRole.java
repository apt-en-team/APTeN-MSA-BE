package com.apten.common.security;

// 공통 사용자 역할이다.
// 플랫폼 전체 운영자와 단지 관리자, 일반 사용자 권한을 공통으로 맞춘다.
public enum UserRole {

    // 플랫폼 전체 운영자 권한이다.
    MASTER,

    // 특정 단지에 배정된 관리자 권한이다.
    ADMIN,

    // 일반 사용자 권한이다.
    USER
}
