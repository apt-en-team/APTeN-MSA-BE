package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleReRegisterReq;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCancelRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleReRegisterRes;
import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import com.apten.parkingvehicle.domain.repository.VisitorVehicleRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 방문차량 등록, 조회, 만료 흐름을 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class VisitorVehicleService {

    // 방문차량 원본 저장소
    private final VisitorVehicleRepository visitorVehicleRepository;

    // 방문차량을 등록한다.
    public VisitorVehicleCreateRes createVisitorVehicle(VisitorVehicleCreateReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문 예정일 유효성 검증
        //TODO 사용자 소속 세대 확인
        //TODO APPROVED 상태로 저장
        //TODO 상태 변경 이벤트 또는 집계 대상 여부 확인
        return VisitorVehicleCreateRes.builder()
                .visitorVehicleId(null)
                .licensePlate(request.getLicensePlate())
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(VisitorVehicleStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 내 방문차량 목록을 조회한다.
    public PageResponse<VisitorVehicleListRes> getMyVisitorVehicleList(VisitorVehicleListReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 로그인 사용자 기준 방문차량 목록 조회
        //TODO 상태와 기간 필터 적용
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 내 방문차량 상세를 조회한다.
    public VisitorVehicleDetailRes getMyVisitorVehicleDetail(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문차량 단건 + 소유자 동시 검증
        //TODO 단지 일치 검증
        //TODO 응답 DTO 매핑
        return VisitorVehicleDetailRes.builder()
                .visitorVehicleId(visitorVehicleId)
                .build();
    }

    // 방문차량 정보를 수정한다.
    public VisitorVehiclePatchRes updateVisitorVehicle(Long visitorVehicleId, VisitorVehiclePatchReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문 예정일 유효성 검증
        //TODO 사용자 소속 세대 확인
        //TODO 방문차량 소유자 검증
        //TODO APPROVED 상태 차량만 수정 가능 여부 확인
        return VisitorVehiclePatchRes.builder()
                .visitorVehicleId(visitorVehicleId)
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량을 취소한다.
    public VisitorVehicleCancelRes cancelVisitorVehicle(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문차량 존재 여부 확인
        //TODO 사용자 소속 세대 확인
        //TODO APPROVED/CANCELLED/DELETED 상태 처리
        //TODO 상태 변경 이벤트 또는 집계 대상 여부 확인
        return VisitorVehicleCancelRes.builder()
                .visitorVehicleId(visitorVehicleId)
                .status(VisitorVehicleStatus.CANCELLED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량을 삭제한다.
    public VisitorVehicleDeleteRes deleteVisitorVehicle(Long visitorVehicleId, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 방문차량 존재 여부 확인
        //TODO 사용자 소속 세대 확인
        //TODO APPROVED/CANCELLED/DELETED 상태 처리
        return VisitorVehicleDeleteRes.builder()
                .message("방문차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량을 재등록한다.
    public VisitorVehicleReRegisterRes reRegisterVisitorVehicle(Long visitorVehicleId, VisitorVehicleReRegisterReq request, Long userId, String userRole, Long complexId) {
        //TODO 입주민 컨텍스트 검증
        //TODO 기존 방문차량 존재 여부 확인
        //TODO 방문 예정일 유효성 검증
        //TODO 사용자 소속 세대 확인
        //TODO sourceId를 유지한 신규 방문차량 등록
        return VisitorVehicleReRegisterRes.builder()
                .visitorVehicleId(null)
                .sourceVisitorVehicleId(visitorVehicleId)
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(VisitorVehicleStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자가 방문차량을 등록한다.
    public AdminVisitorVehicleCreateRes createAdminVisitorVehicle(AdminVisitorVehicleCreateReq request, String userRole, Long complexId, Long selectedComplexId) {
        //TODO 관리자 컨텍스트 해석
        //TODO 세대 존재 여부 확인
        //TODO 방문 예정일 유효성 검증
        //TODO 관리자가 지정한 세대로 방문차량 등록
        return AdminVisitorVehicleCreateRes.builder()
                .visitorVehicleId(null)
                .householdId(request.getHouseholdId())
                .licensePlate(request.getLicensePlate())
                .visitDate(request.getVisitDate())
                .status(VisitorVehicleStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 방문 예정 차량 목록을 조회한다.
    public PageResponse<AdminVisitorVehicleListRes> getAdminVisitorVehicleList(AdminVisitorVehicleListReq request, String userRole, Long complexId, Long selectedComplexId) {
        //TODO 관리자 컨텍스트 해석
        //TODO targetDateType 기준 기간 조건 계산
        //TODO 단지 기준 방문 예정 차량 목록 조회
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 관리자 방문차량 상세를 조회한다.
    public AdminVisitorVehicleDetailRes getAdminVisitorVehicleDetail(Long visitorVehicleId, String userRole, Long complexId, Long selectedComplexId) {
        //TODO 관리자 컨텍스트 해석
        //TODO 방문차량 단건 존재 확인
        //TODO 단지 일치 검증
        //TODO 입주민과 세대 캐시 조회 매핑
        //TODO 응답 DTO 매핑
        return AdminVisitorVehicleDetailRes.builder()
                .visitorVehicleId(visitorVehicleId)
                .build();
    }

    // 방문차량 자동 만료를 처리한다.
    public VisitorVehicleExpireRes expireVisitorVehicles() {
        //TODO visitDate가 지난 APPROVED 방문차량 조회
        //TODO EXPIRED 상태로 변경
        //TODO 만료 처리 건수 반환
        return VisitorVehicleExpireRes.builder()
                .expiredCount(0)
                .executedAt(LocalDateTime.now())
                .build();
    }

    // 입주민 API 헤더 컨텍스트 검증, USER 권한과 식별자 필수
    private void validateResidentContext(Long userId, String userRole, Long complexId) {
        // userRole 빈값 검증
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // USER 권한 아니면 입주민 API 차단
        if (!"USER".equals(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        // userId, complexId 필수값 검증
        if (userId == null || complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 관리자 주차 API의 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    // MASTER는 선택 단지, 일반 관리자는 토큰 단지 기준으로 처리한다.
    private Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if ("MASTER".equals(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
