package com.apten.apartmentcomplex.infrastructure.client.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Auth Service 내부 관리자 수정 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAdminUpdateRes {

    private Long userId;
    private String adminRole;
    private String status;
    private LocalDateTime updatedAt;
}
