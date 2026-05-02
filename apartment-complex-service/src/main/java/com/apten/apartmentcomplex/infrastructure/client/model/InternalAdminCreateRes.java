package com.apten.apartmentcomplex.infrastructure.client.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Auth Service 내부 관리자 생성 응답 예시 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAdminCreateRes {

    private Long userId;
    private String email;
    private String name;
    private String role;
    private String adminRole;
    private LocalDateTime createdAt;
}
