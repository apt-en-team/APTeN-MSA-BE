package com.apten.apartmentcomplex.application.model.response;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 단지 상세 응답 DTO
// 단지 상세 화면에서 필요한 전체 기본 정보를 담는다
@Getter
@Builder
public class ApartmentComplexGetDetailRes {
    private final Long apartmentComplexId;
    private final String code;
    private String name;
    private String address;
    private String addressDetail;
    private String zipcode;
    private ApartmentComplexStatus status;
    private String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
