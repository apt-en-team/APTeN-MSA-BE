package com.apten.auth.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

//추가 단지 서비스 내부 연동용 관리자 수정 응답 DTO
@Getter
@Builder
public class InternalAdminPatchRes {

    private Long userId;
    private String adminRole;
    private String status;
    private LocalDateTime updatedAt;
}
