package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 단지 목록 아이템 응답 DTO
// 목록 화면에서 보여줄 단지 요약 정보를 담는다
@Getter
@Builder
public class ApartmentComplexGetRes {
    private final String code;
    private final String name;
    private final String address;
    private final String status;
    private final String statusName;
    private final String description;
    private final LocalDateTime createdAt;
}
