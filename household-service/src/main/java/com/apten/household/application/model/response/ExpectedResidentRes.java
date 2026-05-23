package com.apten.household.application.model.response;

import com.apten.household.domain.entity.ExpectedResident;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 명부 응답 DTO이다.
@Getter
@Builder
public class ExpectedResidentRes {

    private Long expectedResidentId;
    private Long complexId;
    private Long householdId;
    private String building;
    private String unit;
    private String name;
    private String phone;
    private LocalDate birthDate;
    private String relationship;
    private HouseholdMemberRole householdRole;
    private ExpectedResidentStatus status;
    private Long matchedUserId;
    private LocalDateTime matchedAt;
    private LocalDateTime createdAt;

    public static ExpectedResidentRes from(ExpectedResident expectedResident) {
        return ExpectedResidentRes.builder()
                .expectedResidentId(expectedResident.getId())
                .complexId(expectedResident.getComplexId())
                .householdId(expectedResident.getHouseholdId())
                .building(expectedResident.getBuilding())
                .unit(expectedResident.getUnit())
                .name(expectedResident.getName())
                .phone(expectedResident.getPhone())
                .birthDate(expectedResident.getBirthDate())
                .relationship(expectedResident.getRelationship())
                .householdRole(expectedResident.getHouseholdRole())
                .status(expectedResident.getStatus())
                .matchedUserId(expectedResident.getMatchedUserId())
                .matchedAt(expectedResident.getMatchedAt())
                .createdAt(expectedResident.getCreatedAt())
                .build();
    }
}
