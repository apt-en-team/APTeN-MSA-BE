package com.apten.apartmentcomplex.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 세대 유형 삭제 응답 DTO
// 삭제 완료 메시지를 내려줄 때 사용한다
@Getter
@Builder
public class HouseholdTypeDeleteRes {
    private final String message;
}
