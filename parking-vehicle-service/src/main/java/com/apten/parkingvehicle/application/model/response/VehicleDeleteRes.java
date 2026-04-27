package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 삭제 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDeleteRes {

    // 결과 메시지이다.
    private String message;

    // 삭제 시각이다.
    private LocalDateTime deletedAt;
}
