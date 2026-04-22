package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.GxProgramCancelReq;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.application.model.request.GxProgramPostReq;
import com.apten.facilityreservation.application.model.request.GxProgramSearchReq;
import com.apten.facilityreservation.application.model.response.GxProgramCancelRes;
import com.apten.facilityreservation.application.model.response.GxProgramPatchRes;
import com.apten.facilityreservation.application.model.response.GxProgramPostRes;
import com.apten.facilityreservation.application.model.response.GxProgramRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// GX 프로그램 응용 서비스
// GX 프로그램 조회와 등록, 수정, 취소 시그니처를 이 서비스에 모아둔다
@Service
public class GxProgramService {

    public List<GxProgramRes> getGxProgramList(GxProgramSearchReq request) {
        // TODO: GX 프로그램 목록 조회 로직 구현
        return List.of();
    }

    public GxProgramPostRes createGxProgram(GxProgramPostReq request) {
        // TODO: GX 프로그램 등록 로직 구현
        return GxProgramPostRes.builder().programName(request.getProgramName()).createdAt(LocalDateTime.now()).build();
    }

    public GxProgramPatchRes updateGxProgram(String gxProgramUid, GxProgramPatchReq request) {
        // TODO: GX 프로그램 수정 로직 구현
        return GxProgramPatchRes.builder().gxProgramUid(gxProgramUid).programName(request.getProgramName()).updatedAt(LocalDateTime.now()).build();
    }

    public GxProgramCancelRes cancelGxProgram(String gxProgramUid, GxProgramCancelReq request) {
        // TODO: GX 프로그램 취소 로직 구현
        // TODO: 최소 인원 미달 취소 정책 처리
        return GxProgramCancelRes.builder()
                .gxProgramUid(gxProgramUid)
                .status("CANCELLED")
                .cancelledAt(LocalDateTime.now())
                .reason(request.getReason())
                .build();
    }
}
