package com.apten.apartmentcomplex.application.model.request;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 상태 변경 요청 DTO이다.
// 단지 활성 또는 비활성 상태를 별도 API로 변경할 때 사용한다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexStatusPatchReq {

    // 변경할 단지 상태이다.
    private ApartmentComplexStatus status;
}
