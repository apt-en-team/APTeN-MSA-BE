package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 삭제 응답 DTO이다.
@Getter
@Builder
public class FacilityDeleteRes {

    // 시설 ID이다.
    private Long facilityId;

    // 삭제 시각이다.
    private LocalDateTime deletedAt;
}
