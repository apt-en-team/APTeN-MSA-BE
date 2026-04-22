package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 헬스장 이용 현황 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymStatusRes {
    private LocalDate targetDate;
    private Integer reservedCount;
    private List<GymReservationUserRes> users;
}
