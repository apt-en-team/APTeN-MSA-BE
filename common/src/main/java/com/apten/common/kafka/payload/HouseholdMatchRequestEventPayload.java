package com.apten.common.kafka.payload;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 후 세대 매칭 요청을 생성하기 위한 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchRequestEventPayload {

    // 매칭 대상 사용자 ID이다.
    private Long userId;

    // 가입자가 선택한 단지 ID이다.
    private Long complexId;

    // 가입자가 입력한 이름이다.
    private String name;

    // 가입자가 입력한 연락처이다.
    private String phone;

    // 가입자가 입력한 생년월일이다.
    private LocalDate birthDate;

    // 가입자가 입력한 동 정보이다.
    private String building;

    // 가입자가 입력한 호 정보이다.
    private String unit;
}
