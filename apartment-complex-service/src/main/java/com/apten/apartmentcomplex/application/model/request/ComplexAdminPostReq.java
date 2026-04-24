package com.apten.apartmentcomplex.application.model.request;

import com.apten.apartmentcomplex.domain.enums.ComplexAdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 단지 소속 지정 요청 DTO
// 어떤 사용자를 어떤 역할로 지정할지 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexAdminPostReq {
    private String userId;
    private ComplexAdminRole adminRole;
}
