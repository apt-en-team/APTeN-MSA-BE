package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.AdminVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.VehicleListReq;
import com.apten.parkingvehicle.application.model.request.VehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VehicleRejectReq;
import com.apten.parkingvehicle.application.model.response.AdminVehicleListRes;
import com.apten.parkingvehicle.application.model.response.LicensePlateCheckRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VehicleApproveRes;
import com.apten.parkingvehicle.application.model.response.VehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VehicleListRes;
import com.apten.parkingvehicle.application.model.response.VehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VehicleRejectRes;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 차량 등록, 수정, 승인 흐름을 담당하는 응용 서비스이다.
@Service
public class VehicleService {

    // 차량 등록 신청을 처리한다.
    public VehicleCreateRes createVehicle(VehicleCreateReq request) {
        //TODO user_cache에서 로그인 사용자 확인
        //TODO household_cache에서 사용자 소속 세대 확인
        //TODO 차량번호 중복 여부 확인
        //TODO 차량 정책 기준 등록 제한 검증
        //TODO PENDING 상태 차량 저장
        //TODO 필요 시 알림/이벤트 outbox 적재
        return VehicleCreateRes.builder()
                .vehicleId(null)
                .licensePlate(request.getLicensePlate())
                .modelName(request.getModelName())
                .vehicleType(request.getVehicleType() != null ? request.getVehicleType() : VehicleType.CAR)
                .status(VehicleStatus.PENDING)
                .isPrimary(request.getIsPrimary())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 차량 정보를 수정한다.
    public VehiclePatchRes updateVehicle(Long vehicleId, VehiclePatchReq request) {
        //TODO 로그인 사용자 확인
        //TODO 차량 존재 여부 확인
        //TODO 차량 소유자 검증
        //TODO 수정 가능한 상태인지 확인
        //TODO 차량 기본 정보 수정
        return VehiclePatchRes.builder()
                .vehicleId(vehicleId)
                .modelName(request.getModelName())
                .vehicleType(request.getVehicleType())
                .isPrimary(request.getIsPrimary())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 차량을 소프트 삭제한다.
    public VehicleDeleteRes deleteVehicle(Long vehicleId) {
        //TODO 차량 존재 여부 확인
        //TODO 차량 소유자 검증
        //TODO DELETED 상태 및 isDeleted 처리
        //TODO 승인 차량 삭제 시 차량 상태 변경 이벤트 outbox 적재
        return VehicleDeleteRes.builder()
                .message("차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 내 차량 목록을 조회한다.
    public PageResponse<VehicleListRes> getMyVehicleList(VehicleListReq request) {
        //TODO 로그인 사용자 기준 차량 목록 조회
        //TODO 상태 필터 적용
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 차량번호 중복 여부를 확인한다.
    public LicensePlateCheckRes checkDuplicateLicensePlate(String licensePlate) {
        //TODO 로그인 사용자 또는 관리자 컨텍스트에서 complexId 확인
        //TODO 단지 기준 차량번호 중복 여부 확인
        return LicensePlateCheckRes.builder()
                .isDuplicate(false)
                .build();
    }

    // 차량을 승인한다.
    public VehicleApproveRes approveVehicle(Long vehicleId) {
        //TODO 차량 존재 여부 확인
        //TODO 차량 상태가 PENDING인지 확인
        //TODO 현재 승인 차량 수 조회
        //TODO vehicle_policy 기준 승인 가능 여부 확인
        //TODO APPROVED 상태 변경 및 approvedAt 저장
        //TODO 차량 상태 변경 이벤트 outbox 적재
        return VehicleApproveRes.builder()
                .vehicleId(vehicleId)
                .status(VehicleStatus.APPROVED)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    // 차량을 거절한다.
    public VehicleRejectRes rejectVehicle(Long vehicleId, VehicleRejectReq request) {
        //TODO 차량 존재 여부 확인
        //TODO 차량 상태가 PENDING인지 확인
        //TODO REJECTED 상태 변경 및 거절 사유 저장
        //TODO 차량 상태 변경 이벤트 outbox 적재
        return VehicleRejectRes.builder()
                .vehicleId(vehicleId)
                .status(VehicleStatus.REJECTED)
                .rejectReason(request.getRejectReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 차량 목록을 조회한다.
    public PageResponse<AdminVehicleListRes> getAdminVehicleList(AdminVehicleListReq request) {
        //TODO 관리자 소속 단지 확인
        //TODO 단지 기준 차량 목록 조회
        //TODO 동, 호, 상태, 키워드 필터 적용
        return PageResponse.empty(request.getPage(), request.getSize());
    }
}
