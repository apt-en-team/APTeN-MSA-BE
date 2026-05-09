package com.apten.apartmentcomplex.infrastructure.client.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Auth Service 내부 관리자 삭제 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAdminDeleteRes {

    private Long userId;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
}
