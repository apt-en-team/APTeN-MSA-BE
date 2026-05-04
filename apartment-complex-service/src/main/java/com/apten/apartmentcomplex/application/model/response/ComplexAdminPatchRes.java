package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 단지 관리자 권한 수정 응답 DTO이다.
@Getter
@Builder
public class ComplexAdminPatchRes {

    private final Long userId;
    private final String name;
    private final String email;
    private final String phone;
    private final String adminRole;
    private final String adminRoleName;
    private final Boolean isActive;
    private final LocalDateTime updatedAt;
}
