package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 수정 요청 DTO
// 단지명과 설명만 수정할 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexPatchReq {

    private String name;
    private String description;
}
