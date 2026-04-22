package com.apten.auth.domain.enums;

// 회원 테이블에 저장하는 사용자 권한
public enum UserRole {
    USER,
    ADMIN;

    // 공통 사용자 컨텍스트용 역할로 변환한다
    public com.apten.common.security.UserRole toCommonUserRole() {
        return this == ADMIN ? com.apten.common.security.UserRole.ADMIN : com.apten.common.security.UserRole.RESIDENT;
    }
}
