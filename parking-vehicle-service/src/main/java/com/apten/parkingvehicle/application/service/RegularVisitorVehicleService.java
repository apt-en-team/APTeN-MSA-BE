package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.RegularVisitorVehiclePostReq;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleSearchReq;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleGetPageRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehiclePostRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 고정 방문차량 등록과 삭제 흐름을 담당하는 응용 서비스
@Service
public class RegularVisitorVehicleService {

    public RegularVisitorVehiclePostRes createRegularVisitorVehicle(RegularVisitorVehiclePostReq request) {
        // TODO: 고정 방문차량 등록 로직 구현
        return RegularVisitorVehiclePostRes.builder()
                .licensePlate(request.getLicensePlate())
                .visitorName(request.getVisitorName())
                .phone(request.getPhone())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public RegularVisitorVehicleGetPageRes getMyRegularVisitorVehicleList(RegularVisitorVehicleSearchReq request) {
        // TODO: 고정 방문차량 목록 조회 로직 구현
        return RegularVisitorVehicleGetPageRes.empty(request.getPage(), request.getSize());
    }

    public RegularVisitorVehicleDeleteRes deleteRegularVisitorVehicle(String regularVisitorVehicleUid) {
        // TODO: 고정 방문차량 삭제 로직 구현
        return RegularVisitorVehicleDeleteRes.builder()
                .message("고정 방문차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public AdminRegularVisitorVehicleDeleteRes deleteRegularVisitorVehicleByAdmin(String regularVisitorVehicleUid) {
        // TODO: 관리자 고정 방문차량 강제 삭제 로직 구현
        return AdminRegularVisitorVehicleDeleteRes.builder()
                .message("고정 방문차량 강제 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
