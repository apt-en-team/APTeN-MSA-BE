package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.GxBulkApproveReq;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxWaitingSearchReq;
import com.apten.facilityreservation.application.model.request.MyGxReservationSearchReq;
import com.apten.facilityreservation.application.model.response.GxBulkApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxWaitingPageRes;
import com.apten.facilityreservation.application.model.response.MyGxReservationGetPageRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// GX 예약 응용 서비스
// GX 신청과 조회, 취소, 대기 목록, 일괄 승인 시그니처를 이 서비스에 모아둔다
@Service
public class GxReservationService {

    public GxReservationPostRes createGxReservation(GxReservationPostReq request) {
        // TODO: GX 예약 신청 로직 구현
        return GxReservationPostRes.builder()
                .gxProgramUid(request.getGxProgramUid())
                .status("WAITING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public MyGxReservationGetPageRes getMyGxReservationList(MyGxReservationSearchReq request) {
        // TODO: 내 GX 예약 목록 조회 로직 구현
        return MyGxReservationGetPageRes.empty(request.getPage(), request.getSize());
    }

    public GxReservationCancelRes cancelGxReservation(String gxReservationUid) {
        // TODO: GX 예약 취소 로직 구현
        return GxReservationCancelRes.builder()
                .gxReservationUid(gxReservationUid)
                .status("CANCELLED")
                .cancelledAt(LocalDateTime.now())
                .build();
    }

    public GxWaitingPageRes getGxWaitingList(GxWaitingSearchReq request) {
        // TODO: GX 승인 대기 목록 조회 로직 구현
        return GxWaitingPageRes.empty(request.getPage(), request.getSize());
    }

    public GxBulkApproveRes bulkApproveGxReservation(GxBulkApproveReq request) {
        // TODO: GX 일괄 승인 로직 구현
        return GxBulkApproveRes.builder()
                .gxProgramUid(request.getGxProgramUid())
                .approvedCount(0)
                .rejectedCount(0)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
