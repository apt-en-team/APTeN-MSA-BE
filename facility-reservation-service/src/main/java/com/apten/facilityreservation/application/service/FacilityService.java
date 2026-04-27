package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeSearchReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySearchReq;
import com.apten.facilityreservation.application.model.response.FacilityActivePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePostRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeRes;
import com.apten.facilityreservation.application.model.response.FacilityDeleteRes;
import com.apten.facilityreservation.application.model.response.FacilityGetDetailRes;
import com.apten.facilityreservation.application.model.response.FacilityGetRes;
import com.apten.facilityreservation.application.model.response.FacilityPatchRes;
import com.apten.facilityreservation.application.model.response.FacilityPostRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 시설 도메인 응용 서비스
// 시설 타입과 시설 조회, 등록, 수정, 삭제, 차단 시간 시그니처를 이 서비스에 모아둔다
@Service
public class FacilityService {

    public List<FacilityTypeRes> getFacilityTypeList() {
        // TODO: 시설 유형 목록 조회 로직 구현
        return List.of();
    }

    public List<FacilityGetRes> getFacilityList(FacilitySearchReq request) {
        // TODO: 시설 목록 조회 로직 구현
        return List.of();
    }

    public FacilityGetDetailRes getFacilityDetail(String facilityUid) {
        // TODO: 시설 상세 조회 로직 구현
        return FacilityGetDetailRes.builder().build();
    }

    public FacilityPostRes createFacility(FacilityPostReq request) {
        // TODO: 시설 등록 로직 구현
        // TODO: facility_type.type_code와 complexId 기준으로 facility_policy를 조회한다.
        // TODO: 시설별 override 값이 없으면 facility_policy의 기본 요금과 예약 단위를 사용한다.
        return FacilityPostRes.builder().name(request.getName()).createdAt(LocalDateTime.now()).build();
    }

    public FacilityPatchRes updateFacility(String facilityUid, FacilityPatchReq request) {
        // TODO: 시설 수정 로직 구현
        // TODO: 시설별 override 값과 시설 타입 기본 정책의 우선순위를 함께 관리한다.
        return FacilityPatchRes.builder().facilityUid(facilityUid).name(request.getName()).updatedAt(LocalDateTime.now()).build();
    }

    public FacilityDeleteRes deleteFacility(String facilityUid) {
        // TODO: 시설 삭제 가능 여부 검증 후 삭제 처리
        return FacilityDeleteRes.builder().message("시설 삭제 완료").deletedAt(LocalDateTime.now()).build();
    }

    public FacilityActivePatchRes changeFacilityActive(String facilityUid, FacilityActivePatchReq request) {
        // TODO: 시설 활성 상태 변경 로직 구현
        return FacilityActivePatchRes.builder()
                .facilityUid(facilityUid)
                .isActive(request.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public FacilityBlockTimePostRes createFacilityBlockTime(String facilityUid, FacilityBlockTimePostReq request) {
        // TODO: 시설 차단 시간 등록 로직 구현
        return FacilityBlockTimePostRes.builder()
                .facilityUid(facilityUid)
                .blockDate(request.getBlockDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .reason(request.getReason())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<FacilityBlockTimeRes> getFacilityBlockTimeList(String facilityUid, FacilityBlockTimeSearchReq request) {
        // TODO: 시설 차단 시간 조회 로직 구현
        return List.of();
    }
}
