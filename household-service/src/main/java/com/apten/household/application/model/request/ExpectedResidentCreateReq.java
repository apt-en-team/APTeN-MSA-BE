package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdMemberRole;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 명부 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpectedResidentCreateReq {

    // 명부를 연결할 세대 ID이다.
    private Long householdId;

    // 입주민 이름이다.
    private String name;

    // 입주민 연락처이다.
    private String phone;

    // 입주민 생년월일이다.
    private LocalDate birthDate;

    private LocalDate moveInDate;

    // 세대주와의 관계이다.
    private String relationship;

    // 세대 내 권한이다.
    private HouseholdMemberRole householdRole;
}
