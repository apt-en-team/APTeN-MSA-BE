package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// household-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 세대 등록과 수정에서 공통으로 받을 값만 먼저 정리해 둔다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdBaseRequest {

    // 세대 이름 입력값
    private String name;

    // 세대 상태 입력값
    private HouseholdStatus status;
}
