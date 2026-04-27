package com.apten.household.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 매칭 요청 생성용 내부 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchPostReq {

    // 매칭 대상 사용자 ID이다.
    private Long userId;

    // 매칭 대상 단지 ID이다.
    private Long complexId;

    // 입력 이름이다.
    private String inputName;

    // 입력 전화번호이다.
    private String inputPhone;

    // 입력 생년월일이다.
    private LocalDate inputBirthDate;

    // 입력 동 정보이다.
    private String inputBuilding;

    // 입력 호 정보이다.
    private String inputUnit;
}
