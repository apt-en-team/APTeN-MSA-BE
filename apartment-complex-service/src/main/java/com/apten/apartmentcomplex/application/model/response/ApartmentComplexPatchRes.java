package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 단지 수정 응답 DTO
// 수정 완료 후 변경된 단지 식별 정보와 시각을 내려줄 때 사용한다
@Getter
@Builder
public class ApartmentComplexPatchRes {
    private final String code;
    private final String name;
    private final String description;
    private final LocalDateTime updatedAt;
}
