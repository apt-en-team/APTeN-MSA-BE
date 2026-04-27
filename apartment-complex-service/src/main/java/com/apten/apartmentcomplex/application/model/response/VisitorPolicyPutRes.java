package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 방문차량 정책 설정 응답 DTO
// 단지 방문차량 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class VisitorPolicyPutRes {

    // 단지 코드
    private String code;

    // 무료 시간
    private Integer freeMinutes;

    // 시간당 요금
    private BigDecimal hourFee;

    // 월 기준 시간
    private Integer monthlyLimitHours;

    // 수정일시
    private LocalDateTime updatedAt;
}