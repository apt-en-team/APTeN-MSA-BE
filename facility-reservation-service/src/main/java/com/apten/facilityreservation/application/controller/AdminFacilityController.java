package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.CountStatusReq;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeBatchPostReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeListReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityListReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPatchReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatBulkPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPostReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityTypeListReq;
import com.apten.facilityreservation.application.model.request.FacilityUsageStatusReq;
import com.apten.facilityreservation.application.model.request.SeatStatusReq;
import com.apten.facilityreservation.application.model.response.CountStatusRes;
import com.apten.facilityreservation.application.model.response.FacilityActivePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeBatchDeactivateRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeBatchPostRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeDeactivateRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeListRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePostRes;
import com.apten.facilityreservation.application.model.response.FacilityDeleteRes;
import com.apten.facilityreservation.application.model.response.FacilityDetailRes;
import com.apten.facilityreservation.application.model.response.FacilityListRes;
import com.apten.facilityreservation.application.model.response.FacilityPatchRes;
import com.apten.facilityreservation.application.model.response.FacilityPostRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatListRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPatchRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatBulkPostRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPostRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeListRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityUsageStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.SeatStatusRes;
import com.apten.facilityreservation.application.service.FacilityService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// 관리자 시설/좌석/현황 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminFacilityController {

    private final FacilityService facilityService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // 시설 등록
    @PostMapping("/facilities")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityPostRes> createFacility(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody FacilityPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 등록 성공", facilityService.createFacility(context.getComplexId(), req));
    }

    // 관리자 시설 목록 조회
    @GetMapping("/facilities")
    public ResultResponse<PageResponse<FacilityListRes>> getAdminFacilityList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute FacilityListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 시설 목록 조회 성공", facilityService.getAdminFacilityList(context.getComplexId(), req));
    }

    // 관리자 시설 상세 조회
    @GetMapping("/facilities/{facilityId}")
    public ResultResponse<FacilityDetailRes> getAdminFacilityDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 시설 상세 조회 성공", facilityService.getAdminFacilityDetail(context.getComplexId(), facilityId));
    }

    // 시설 수정
    @PatchMapping("/facilities/{facilityId}")
    public ResultResponse<FacilityPatchRes> updateFacility(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody FacilityPatchReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 수정 성공", facilityService.updateFacility(context.getComplexId(), facilityId, req));
    }

    // 시설 삭제
    @DeleteMapping("/facilities/{facilityId}")
    public ResultResponse<FacilityDeleteRes> deleteFacility(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 삭제 성공", facilityService.deleteFacility(context.getComplexId(), facilityId));
    }

    // 시설 활성/비활성 변경
    @PatchMapping("/facilities/{facilityId}/active")
    public ResultResponse<FacilityActivePatchRes> changeFacilityActive(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody FacilityActivePatchReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 활성 상태 변경 성공", facilityService.changeFacilityActive(context.getComplexId(), facilityId, req));
    }

    // 시설 타입 목록 조회
    @GetMapping("/facility-types")
    public ResultResponse<List<FacilityTypeListRes>> getFacilityTypeList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute FacilityTypeListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 타입 목록 조회 성공", facilityService.getFacilityTypeList(context.getComplexId(), req));
    }

//    // 시설 타입 수정
//    @PatchMapping("/api/admin/facility-types/{facilityTypeId}")
//    public ResultResponse<FacilityTypePatchRes> updateFacilityType(
//            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
//            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
//            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
//            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
//            @PathVariable Long facilityTypeId,
//            @RequestBody FacilityTypePatchReq req
//    ) {
//        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
//        return ResultResponse.success("시설 타입 수정 성공", facilityService.updateFacilityType(context.getComplexId(), facilityTypeId, req));
//    }

    // 시설 차단 시간 등록
    @PostMapping("/facilities/{facilityId}/block-times")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityBlockTimePostRes> createFacilityBlockTime(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody FacilityBlockTimePostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 차단 시간 등록 성공", facilityService.createFacilityBlockTime(context.getComplexId(), facilityId, req));
    }

    // 시설 차단 시간 조회
    @GetMapping("/facilities/{facilityId}/block-times")
    public ResultResponse<List<FacilityBlockTimeListRes>> getFacilityBlockTimeList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @ModelAttribute FacilityBlockTimeListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 차단 시간 조회 성공", facilityService.getFacilityBlockTimeList(context.getComplexId(), facilityId, req));
    }

    // 반복 차단 배치 등록 (요일 + 기간)
    @PostMapping("/facilities/{facilityId}/block-times/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityBlockTimeBatchPostRes> createFacilityBlockTimeBatch(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody FacilityBlockTimeBatchPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("반복 차단 시간 배치 등록 성공", facilityService.createFacilityBlockTimeBatch(context.getComplexId(), facilityId, req));
    }

    // 반복 차단 그룹 비활성화 (batchId 기준)
    @PatchMapping("/facilities/{facilityId}/block-times/batch/{batchId}/deactivate")
    public ResultResponse<FacilityBlockTimeBatchDeactivateRes> deactivateFacilityBlockTimeBatch(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @PathVariable Long batchId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("반복 차단 그룹 비활성화 성공", facilityService.deactivateFacilityBlockTimeBatch(context.getComplexId(), facilityId, batchId));
    }

    // 시설 차단 시간 단건 비활성화
    @PatchMapping("/facilities/{facilityId}/block-times/{blockTimeId}/deactivate")
    public ResultResponse<FacilityBlockTimeDeactivateRes> deactivateFacilityBlockTime(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @PathVariable Long blockTimeId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 차단 시간 비활성화 성공", facilityService.deactivateFacilityBlockTime(context.getComplexId(), facilityId, blockTimeId));
    }

    // 시설 차단 시간 단건 수정
    @PatchMapping("/facilities/{facilityId}/block-times/{blockTimeId}")
    public ResultResponse<FacilityBlockTimePatchRes> updateFacilityBlockTime(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @PathVariable Long blockTimeId,
            @RequestBody FacilityBlockTimePatchReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 차단 시간 수정 성공", facilityService.updateFacilityBlockTime(context.getComplexId(), facilityId, blockTimeId, req));
    }

    // 정기 휴무 규칙 등록
    @PostMapping("/facilities/{facilityId}/closure-rules")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<com.apten.facilityreservation.application.model.response.FacilityClosureRulePostRes> createClosureRule(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody com.apten.facilityreservation.application.model.request.FacilityClosureRulePostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("정기 휴무 규칙 등록 성공", facilityService.createClosureRule(context.getComplexId(), facilityId, req));
    }

    // 정기 휴무 규칙 목록 조회
    @GetMapping("/facilities/{facilityId}/closure-rules")
    public ResultResponse<List<com.apten.facilityreservation.application.model.response.FacilityClosureRuleListRes>> getClosureRuleList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("정기 휴무 규칙 목록 조회 성공", facilityService.getClosureRuleList(context.getComplexId(), facilityId));
    }

    // 정기 휴무 규칙 수정
    @PatchMapping("/facilities/{facilityId}/closure-rules/{ruleId}")
    public ResultResponse<com.apten.facilityreservation.application.model.response.FacilityClosureRuleListRes> updateClosureRule(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @PathVariable Long ruleId,
            @RequestBody com.apten.facilityreservation.application.model.request.FacilityClosureRulePatchReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("정기 휴무 규칙 수정 성공", facilityService.updateClosureRule(context.getComplexId(), facilityId, ruleId, req));
    }

    // 정기 휴무 규칙 비활성화
    @PatchMapping("/facilities/{facilityId}/closure-rules/{ruleId}/deactivate")
    public ResultResponse<com.apten.facilityreservation.application.model.response.FacilityClosureRuleDeactivateRes> deactivateClosureRule(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @PathVariable Long ruleId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("정기 휴무 규칙 비활성화 성공", facilityService.deactivateClosureRule(context.getComplexId(), facilityId, ruleId));
    }

    // 시설 좌석 등록
    @PostMapping("/facilities/{facilityId}/seats")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilitySeatPostRes> createFacilitySeat(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody FacilitySeatPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 좌석 등록 성공", facilityService.createFacilitySeat(context.getComplexId(), facilityId, req));
    }

    // 시설 좌석 일괄 등록
    @PostMapping("/facilities/{facilityId}/seats/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilitySeatBulkPostRes> createFacilitySeatsBulk(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @RequestBody FacilitySeatBulkPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 좌석 일괄 등록 성공", facilityService.createFacilitySeatsBulk(context.getComplexId(), facilityId, req));
    }

    // 시설 좌석 목록 조회
    @GetMapping("/facilities/{facilityId}/seats")
    public ResultResponse<List<FacilitySeatListRes>> getFacilitySeatList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 좌석 목록 조회 성공", facilityService.getFacilitySeatList(context.getComplexId(), facilityId));
    }

    // 시설 좌석 수정
    @PatchMapping("/facility-seats/{seatId}")
    public ResultResponse<FacilitySeatPatchRes> updateFacilitySeat(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long seatId,
            @RequestBody FacilitySeatPatchReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 좌석 수정 성공", facilityService.updateFacilitySeat(context.getComplexId(), seatId, req));
    }

    // 시설 이용 현황 조회
    @GetMapping("/facility-usage/status")
    public ResultResponse<FacilityUsageStatusRes> getFacilityUsageStatus(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute FacilityUsageStatusReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 이용 현황 조회 성공", facilityService.getFacilityUsageStatus(context.getComplexId(), req));
    }

    // 좌석 상태 조회
    @GetMapping("/facilities/{facilityId}/seat-status")
    public ResultResponse<List<SeatStatusRes>> getSeatStatus(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @ModelAttribute SeatStatusReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("좌석 상태 조회 성공", facilityService.getSeatStatus(context.getComplexId(), facilityId, req));
    }

    // 정원형 이용 현황 조회
    @GetMapping("/facilities/{facilityId}/count-status")
    public ResultResponse<CountStatusRes> getCountStatus(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long facilityId,
            @ModelAttribute CountStatusReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("정원형 이용 현황 조회 성공", facilityService.getCountStatus(context.getComplexId(), facilityId, req));
    }
}
