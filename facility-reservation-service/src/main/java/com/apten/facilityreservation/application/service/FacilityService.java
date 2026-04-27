package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.CountStatusReq;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeListReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityListReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPatchReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPostReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePostReq;
import com.apten.facilityreservation.application.model.request.FacilityUsageStatusReq;
import com.apten.facilityreservation.application.model.request.ResidentFacilityListReq;
import com.apten.facilityreservation.application.model.request.SeatStatusReq;
import com.apten.facilityreservation.application.model.response.CountStatusRes;
import com.apten.facilityreservation.application.model.response.FacilityActivePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeListRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePostRes;
import com.apten.facilityreservation.application.model.response.FacilityDeleteRes;
import com.apten.facilityreservation.application.model.response.FacilityDetailRes;
import com.apten.facilityreservation.application.model.response.FacilityListRes;
import com.apten.facilityreservation.application.model.response.FacilityPatchRes;
import com.apten.facilityreservation.application.model.response.FacilityPostRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatListRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPatchRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPostRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeListRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePostRes;
import com.apten.facilityreservation.application.model.response.FacilityUsageStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ResidentFacilityDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilityListRes;
import com.apten.facilityreservation.application.model.response.SeatStatusRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 시설과 시설 타입, 좌석, 차단 시간 관련 API 시그니처를 관리하는 서비스이다.
@Service
public class FacilityService {

    // 관리자 시설 등록을 처리한다.
    public FacilityPostRes createFacility(FacilityPostReq req) {
        //TODO complex_cache에서 단지 활성 상태 확인
        //TODO facility_type 존재 및 활성 여부 확인
        //TODO facility_policy 기준 기본 정책 확인
        //TODO 시설 예약 방식별 필수값 검증
        //TODO facility 저장
        return FacilityPostRes.builder()
                .facilityId(0L)
                .complexId(req.getComplexId())
                .name(req.getName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 시설 목록을 조회한다.
    public PageResponse<FacilityListRes> getAdminFacilityList(FacilityListReq req) {
        //TODO complexId와 typeId 기준 목록 필터링
        //TODO 삭제되지 않은 시설만 조회
        //TODO 페이지 응답으로 변환
        return PageResponse.empty(req.getPage(), req.getSize());
    }

    // 관리자 시설 상세를 조회한다.
    public FacilityDetailRes getAdminFacilityDetail(Long facilityId) {
        //TODO facilityId 기준 시설 상세 조회
        //TODO 시설 타입과 정책 override 정보 조합
        return FacilityDetailRes.builder().facilityId(facilityId).build();
    }

    // 관리자 시설 수정을 처리한다.
    public FacilityPatchRes updateFacility(Long facilityId, FacilityPatchReq req) {
        //TODO 시설 존재 여부 확인
        //TODO 시설 타입 변경 시 type 활성 여부 확인
        //TODO 시설별 override 값과 시설 타입 기본 정책의 우선순위를 함께 관리
        //TODO 시설 수정 저장
        return FacilityPatchRes.builder()
                .facilityId(facilityId)
                .name(req.getName())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 시설 삭제를 처리한다.
    public FacilityDeleteRes deleteFacility(Long facilityId) {
        //TODO 시설 존재 여부 확인
        //TODO 진행 중 예약 또는 향후 예약 존재 여부 확인
        //TODO 예약이 있으면 FACILITY_HAS_RESERVATION 처리
        //TODO isDeleted와 deletedAt으로 소프트 삭제
        return FacilityDeleteRes.builder()
                .facilityId(facilityId)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 시설 활성 상태를 변경한다.
    public FacilityActivePatchRes changeFacilityActive(Long facilityId, FacilityActivePatchReq req) {
        //TODO 시설 존재 여부 확인
        //TODO 활성 또는 비활성 상태 변경
        return FacilityActivePatchRes.builder()
                .facilityId(facilityId)
                .isActive(req.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 타입을 등록한다.
    public FacilityTypePostRes createFacilityType(FacilityTypePostReq req) {
        //TODO typeCode 중복 여부 확인
        //TODO 시설 타입 저장
        return FacilityTypePostRes.builder()
                .facilityTypeId(0L)
                .typeCode(req.getTypeCode())
                .typeName(req.getTypeName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 시설 타입 목록을 조회한다.
    public List<FacilityTypeListRes> getFacilityTypeList() {
        //TODO 활성 여부 기준 시설 타입 목록 조회
        return List.of();
    }

    // 시설 타입을 수정한다.
    public FacilityTypePatchRes updateFacilityType(Long facilityTypeId, FacilityTypePatchReq req) {
        //TODO 시설 타입 존재 여부 확인
        //TODO 타입명과 설명, 활성 여부 수정
        return FacilityTypePatchRes.builder()
                .facilityTypeId(facilityTypeId)
                .typeName(req.getTypeName())
                .isActive(req.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 차단 시간을 등록한다.
    public FacilityBlockTimePostRes createFacilityBlockTime(Long facilityId, FacilityBlockTimePostReq req) {
        //TODO 시설 존재 여부 확인
        //TODO 차단일과 시간대 유효성 검증
        //TODO 차단 시간 저장
        return FacilityBlockTimePostRes.builder()
                .facilityBlockTimeId(0L)
                .facilityId(facilityId)
                .blockDate(req.getBlockDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .reason(req.getReason())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 시설 차단 시간 목록을 조회한다.
    public List<FacilityBlockTimeListRes> getFacilityBlockTimeList(Long facilityId, FacilityBlockTimeListReq req) {
        //TODO 시설 존재 여부 확인
        //TODO blockDate 기준 차단 시간 목록 조회
        return List.of();
    }

    // 시설 좌석을 등록한다.
    public FacilitySeatPostRes createFacilitySeat(Long facilityId, FacilitySeatPostReq req) {
        //TODO facility가 SEAT 방식인지 확인
        //TODO 좌석번호 중복 여부 확인
        //TODO 좌석 등록 처리
        return FacilitySeatPostRes.builder()
                .seatId(0L)
                .facilityId(facilityId)
                .seatNo(req.getSeatNo())
                .seatName(req.getSeatName())
                .isActive(req.getIsActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 시설 좌석 목록을 조회한다.
    public List<FacilitySeatListRes> getFacilitySeatList(Long facilityId) {
        //TODO 시설 존재 여부 확인
        //TODO 시설별 좌석 목록 조회
        return List.of();
    }

    // 시설 좌석을 수정한다.
    public FacilitySeatPatchRes updateFacilitySeat(Long seatId, FacilitySeatPatchReq req) {
        //TODO 좌석 존재 여부 확인
        //TODO 좌석 등록 또는 수정 처리
        return FacilitySeatPatchRes.builder()
                .seatId(seatId)
                .seatName(req.getSeatName())
                .sortOrder(req.getSortOrder())
                .isActive(req.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 입주민 시설 목록을 조회한다.
    public List<ResidentFacilityListRes> getResidentFacilityList(ResidentFacilityListReq req) {
        //TODO 입주민 소속 단지 기준 활성 시설 목록 조회
        return List.of();
    }

    // 입주민 시설 상세를 조회한다.
    public ResidentFacilityDetailRes getResidentFacilityDetail(Long facilityId) {
        //TODO 시설 활성 상태 확인
        //TODO 시설 상세와 적용 정책 조회
        return ResidentFacilityDetailRes.builder().facilityId(facilityId).build();
    }

    // 시설 이용 현황을 조회한다.
    public FacilityUsageStatusRes getFacilityUsageStatus(FacilityUsageStatusReq req) {
        //TODO complexId 기준 시설 이용 현황 조회
        return FacilityUsageStatusRes.builder()
                .complexId(req.getComplexId())
                .targetDate(req.getTargetDate())
                .items(List.of())
                .build();
    }

    // 좌석 상태를 조회한다.
    public SeatStatusRes getSeatStatus(Long facilityId, SeatStatusReq req) {
        //TODO 좌석형 시설인지 확인
        //TODO 좌석 상태와 예약 현황 조회
        return SeatStatusRes.builder()
                .facilityId(facilityId)
                .reservationDate(req.getReservationDate())
                .seats(List.of())
                .build();
    }

    // 정원형 이용 현황을 조회한다.
    public CountStatusRes getCountStatus(Long facilityId, CountStatusReq req) {
        //TODO 정원형 시설인지 확인
        //TODO 같은 시간대 예약 수와 잔여 정원 계산
        return CountStatusRes.builder()
                .facilityId(facilityId)
                .reservationDate(req.getReservationDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .maxCount(0)
                .reservedCount(0)
                .availableCount(0)
                .build();
    }
}
