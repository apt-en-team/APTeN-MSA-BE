package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.VehicleAdminSearchReq;
import com.apten.parkingvehicle.application.model.request.VehicleCheckLicensePlateReq;
import com.apten.parkingvehicle.application.model.request.VehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VehiclePostReq;
import com.apten.parkingvehicle.application.model.request.VehicleRejectReq;
import com.apten.parkingvehicle.application.model.response.VehicleAdminGetPageRes;
import com.apten.parkingvehicle.application.model.response.VehicleApproveRes;
import com.apten.parkingvehicle.application.model.response.VehicleCheckLicensePlateRes;
import com.apten.parkingvehicle.application.model.response.VehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VehiclePostRes;
import com.apten.parkingvehicle.application.model.response.VehicleRejectRes;
import com.apten.parkingvehicle.application.model.response.VehicleRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 입주민 차량 등록과 승인 흐름을 담당하는 응용 서비스
@Service
public class VehicleService {

    public VehiclePostRes createVehicle(VehiclePostReq request) {
        // TODO: 차량 등록 신청 로직 구현
        return VehiclePostRes.builder()
                .licensePlate(request.getLicensePlate())
                .modelName(request.getModelName())
                .vehicleType(request.getVehicleType())
                .status("PENDING")
                .isPrimary(request.getIsPrimary())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VehiclePatchRes updateVehicle(String vehicleUid, VehiclePatchReq request) {
        // TODO: 차량 수정 로직 구현
        return VehiclePatchRes.builder()
                .vehicleUid(vehicleUid)
                .modelName(request.getModelName())
                .vehicleType(request.getVehicleType())
                .isPrimary(request.getIsPrimary())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public VehicleDeleteRes deleteVehicle(String vehicleUid) {
        // TODO: 차량 소프트 삭제 로직 구현
        return VehicleDeleteRes.builder()
                .message("차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public List<VehicleRes> getMyVehicleList() {
        // TODO: 내 차량 목록 조회 로직 구현
        return List.of();
    }

    public VehicleCheckLicensePlateRes checkDuplicateLicensePlate(VehicleCheckLicensePlateReq request) {
        // TODO: 단지 기준 차량번호 중복 검증 구현
        return VehicleCheckLicensePlateRes.builder()
                .isDuplicate(false)
                .build();
    }

    public VehicleApproveRes approveVehicle(String vehicleUid) {
        // TODO: 차량 승인 로직 구현
        // TODO: 차량 승인 이벤트 발행
        return VehicleApproveRes.builder()
                .vehicleUid(vehicleUid)
                .status("APPROVED")
                .approvedAt(LocalDateTime.now())
                .build();
    }

    public VehicleRejectRes rejectVehicle(String vehicleUid, VehicleRejectReq request) {
        // TODO: 차량 거절 로직 구현
        // TODO: 차량 거절 이벤트 발행
        return VehicleRejectRes.builder()
                .vehicleUid(vehicleUid)
                .status("REJECTED")
                .rejectReason(request.getRejectReason())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public VehicleAdminGetPageRes getAdminVehicleList(VehicleAdminSearchReq request) {
        // TODO: 관리자 전체 차량 목록 조회 로직 구현
        return VehicleAdminGetPageRes.empty(request.getPage(), request.getSize());
    }
}
