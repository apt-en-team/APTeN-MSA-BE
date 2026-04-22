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
    private String userUid;
    private String apartmentComplexUid;
    private String name;
    private String phone;
    private LocalDate birthDate;
    private String building;
    private String unit;
}
