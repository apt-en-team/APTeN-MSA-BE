package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaseFeeReflectRes {

    private Long complexId;

    private Integer billYear;

    private Integer billMonth;

    private Integer affectedHouseholdCount;

    private LocalDateTime reflectedAt;
}
