package com.apten.apartmentcomplex.application.model.response;

import com.apten.apartmentcomplex.domain.enums.ComplexAdminRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 단지 소속 지정 응답 DTO
// 지정된 사용자와 역할, 처리 시각을 내려줄 때 사용한다
@Getter
@Builder
public class ComplexAdminPostRes {
    private final String apartmentComplexUid;
    private final String userUid;
    private final ComplexAdminRole adminRole;
    private final LocalDateTime assignedAt;
}
