package com.apten.auth.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

//추가 단지 서비스 내부 연동용 관리자 생성 응답 DTO
@Getter
@Builder
public class InternalAdminCreateRes {

    private Long userId;
    private String email;
    private String name;
    private String phone;
    private String role;
    private String adminRole;
    private LocalDateTime createdAt;
}
