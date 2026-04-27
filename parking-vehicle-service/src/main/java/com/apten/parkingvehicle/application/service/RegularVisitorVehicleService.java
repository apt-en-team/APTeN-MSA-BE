package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.RegularVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.RegularVisitorVehicleListRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 고정 방문차량 등록과 삭제 흐름을 담당하는 응용 서비스이다.
@Service
public class RegularVisitorVehicleService {

    // 고정 방문차량을 등록한다.
    public RegularVisitorVehicleCreateRes createRegularVisitorVehicle(RegularVisitorVehicleCreateReq request) {
        //TODO 사용자 소속 세대 확인
        //TODO 고정 방문차량 등록 정보 저장
        return RegularVisitorVehicleCreateRes.builder()
                .regularVisitorVehicleId(null)
                .licensePlate(request.getLicensePlate())
                .visitorName(request.getVisitorName())
                .phone(request.getPhone())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 고정 방문차량 목록을 조회한다.
    public PageResponse<RegularVisitorVehicleListRes> getMyRegularVisitorVehicleList(RegularVisitorVehicleListReq request) {
        //TODO 로그인 사용자 기준 고정 방문차량 목록 조회
        //TODO 활성 여부 필터 적용
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 고정 방문차량을 삭제한다.
    public RegularVisitorVehicleDeleteRes deleteRegularVisitorVehicle(Long regularVisitorVehicleId) {
        //TODO 고정 방문차량 존재 여부 확인
        //TODO 사용자 소유자 검증
        //TODO isDeleted와 isActive 상태 갱신
        return RegularVisitorVehicleDeleteRes.builder()
                .message("고정 방문차량 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 관리자가 고정 방문차량을 강제 삭제한다.
    public AdminRegularVisitorVehicleDeleteRes deleteRegularVisitorVehicleByAdmin(Long regularVisitorVehicleId) {
        //TODO 고정 방문차량 존재 여부 확인
        //TODO 운영 정책 기준 강제 삭제 처리
        return AdminRegularVisitorVehicleDeleteRes.builder()
                .message("고정 방문차량 강제 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
