package com.apten.auth.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

//추가 관리자 비활성 응답 DTO
@Getter
@Builder
public class AdminDeleteRes {

    private Long userId;
    private String status;
    private LocalDateTime deletedAt;
}
