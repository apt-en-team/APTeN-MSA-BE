package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.HouseholdTypePatchReq;
import com.apten.apartmentcomplex.application.model.request.HouseholdTypePostReq;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypeDeleteRes;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypePatchRes;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypePostRes;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypeRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 세대 유형 응용 서비스
// 세대 유형 등록과 조회, 수정, 삭제 시그니처를 이 서비스에 모아둔다
@Service
public class HouseholdTypeService {

    // 세대 유형 등록 서비스
    public HouseholdTypePostRes createHouseholdType(String apartmentComplexUid, HouseholdTypePostReq request) {
        // TODO: 세대 유형 등록 로직 구현
        return HouseholdTypePostRes.builder()
                .householdTypeName(request.getHouseholdTypeName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 세대 유형 목록 조회 서비스
    public List<HouseholdTypeRes> getHouseholdTypeList(String apartmentComplexUid) {
        // TODO: 세대 유형 목록 조회 로직 구현
        return List.of();
    }

    // 세대 유형 수정 서비스
    public HouseholdTypePatchRes updateHouseholdType(String householdTypeUid, HouseholdTypePatchReq request) {
        // TODO: 세대 유형 수정 로직 구현
        return HouseholdTypePatchRes.builder()
                .householdTypeUid(householdTypeUid)
                .householdTypeName(request.getHouseholdTypeName())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 세대 유형 삭제 서비스
    public HouseholdTypeDeleteRes deleteHouseholdType(String householdTypeUid) {
        // TODO: 세대 유형 삭제 로직 구현
        return HouseholdTypeDeleteRes.builder()
                .message("세대 유형 삭제 완료")
                .build();
    }
}
