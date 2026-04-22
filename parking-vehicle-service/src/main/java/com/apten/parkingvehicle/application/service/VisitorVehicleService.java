package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.AdminVisitorVehiclePostReq;
import com.apten.parkingvehicle.application.model.request.AdminVisitorVehicleSearchReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePostReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleReRegisterReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleSearchReq;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehicleGetPageRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorVehiclePostRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCancelRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleGetPageRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePostRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleReRegisterRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 방문차량 등록과 조회, 자동 만료 흐름을 담당하는 응용 서비스
@Service
public class VisitorVehicleService {

    public VisitorVehiclePostRes createVisitorVehicle(VisitorVehiclePostReq request) {
        // TODO: 방문차량 등록 로직 구현
        return VisitorVehiclePostRes.builder()
                .licensePlate(request.getLicensePlate())
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VisitorVehicleGetPageRes getMyVisitorVehicleList(VisitorVehicleSearchReq request) {
        // TODO: 내 방문차량 목록 조회 로직 구현
        return VisitorVehicleGetPageRes.empty(request.getPage(), request.getSize());
    }

    public VisitorVehiclePatchRes updateVisitorVehicle(String visitorVehicleUid, VisitorVehiclePatchReq request) {
        // TODO: 방문차량 수정 로직 구현
        return VisitorVehiclePatchRes.builder()
                .visitorVehicleUid(visitorVehicleUid)
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public VisitorVehicleCancelRes cancelVisitorVehicle(String visitorVehicleUid) {
        // TODO: 방문차량 취소 로직 구현
        return VisitorVehicleCancelRes.builder()
                .visitorVehicleUid(visitorVehicleUid)
                .status("CANCELLED")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public VisitorVehicleDeleteRes deleteVisitorVehicle(String visitorVehicleUid) {
        // TODO: 방문차량 소프트 삭제 로직 구현
        return VisitorVehicleDeleteRes.builder()
                .message("방문차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public VisitorVehicleReRegisterRes reRegisterVisitorVehicle(String visitorVehicleUid, VisitorVehicleReRegisterReq request) {
        // TODO: 방문차량 재등록 로직 구현
        return VisitorVehicleReRegisterRes.builder()
                .sourceVisitorVehicleUid(visitorVehicleUid)
                .visitDate(request.getVisitDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public AdminVisitorVehiclePostRes createAdminVisitorVehicle(AdminVisitorVehiclePostReq request) {
        // TODO: 관리자 방문차량 등록 로직 구현
        return AdminVisitorVehiclePostRes.builder()
                .householdId(request.getHouseholdId())
                .licensePlate(request.getLicensePlate())
                .visitDate(request.getVisitDate())
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public AdminVisitorVehicleGetPageRes getAdminVisitorVehicleList(AdminVisitorVehicleSearchReq request) {
        // TODO: 관리자 방문 예정 차량 목록 조회 로직 구현
        return AdminVisitorVehicleGetPageRes.empty(request.getPage(), request.getSize());
    }

    public VisitorVehicleExpireRes expireVisitorVehicles() {
        // TODO: 방문차량 자동 만료 스케줄러 처리
        return VisitorVehicleExpireRes.builder()
                .expiredCount(0)
                .executedAt(LocalDateTime.now())
                .build();
    }
}
