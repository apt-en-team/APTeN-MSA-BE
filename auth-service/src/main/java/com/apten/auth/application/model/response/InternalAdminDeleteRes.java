package com.apten.auth.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

//추가 단지 서비스 내부 연동용 관리자 삭제 응답 DTO
@Getter
@Builder
public class InternalAdminDeleteRes {

    private Long userId;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
}
