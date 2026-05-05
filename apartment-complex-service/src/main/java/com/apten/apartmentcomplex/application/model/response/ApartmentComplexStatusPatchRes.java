package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 단지 상태 변경 응답 DTO이다.
// 상태 변경 이후 단지 코드와 현재 상태를 내려줄 때 사용한다.
@Getter
@Builder
public class ApartmentComplexStatusPatchRes {

    // 상태가 변경된 단지 코드이다.
    private final String code;

    // 변경 완료된 단지 상태이다.
    private final String status;

    // 변경 완료된 단지 상태 표시명이다.
    private final String statusName;

    // 상태 변경 처리 시각이다.
    private final LocalDateTime updatedAt;
}
