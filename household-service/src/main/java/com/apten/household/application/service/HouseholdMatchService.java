package com.apten.household.application.service;

import com.apten.household.application.model.request.HouseholdMatchPostReq;
import com.apten.household.application.model.request.HouseholdMatchRejectReq;
import com.apten.household.application.model.request.HouseholdMatchSearchReq;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchGetRes;
import com.apten.household.application.model.response.HouseholdMatchPostRes;
import com.apten.household.application.model.response.PageResponse;
import com.apten.household.application.model.response.HouseholdMatchRejectRes;
import com.apten.household.infrastructure.mapper.HouseholdMatchQueryMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// 세대 매칭 도메인 응용 서비스
// 세대 매칭 요청과 승인 처리 시그니처만 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class HouseholdMatchService {

    // 세대 매칭 도메인 조회용 MyBatis 매퍼
    private final ObjectProvider<HouseholdMatchQueryMapper> householdMatchQueryMapperProvider;

    // 세대 매칭 요청 생성 서비스
    public HouseholdMatchPostRes createMatchRequest(HouseholdMatchPostReq request) {
        // TODO: 세대 매칭 요청 생성 로직 구현
        // TODO: 자동 승인 결과 이벤트 Kafka 발행
        return HouseholdMatchPostRes.builder().createdAt(LocalDateTime.now()).build();
    }

    // 수동 승인 대상 목록 조회 서비스
    public PageResponse<HouseholdMatchGetRes> getMatchRequestList(HouseholdMatchSearchReq request) {
        // TODO: 수동 승인 대상 목록 조회 로직 구현
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 수동 승인 처리 서비스
    public HouseholdMatchApproveRes approveMatchRequest(String matchRequestUid) {
        // TODO: 수동 승인 처리 로직 구현
        // TODO: 세대 승인 결과 이벤트 Kafka 발행
        return HouseholdMatchApproveRes.builder().approvedAt(LocalDateTime.now()).build();
    }

    // 수동 거절 처리 서비스
    public HouseholdMatchRejectRes rejectMatchRequest(
            String matchRequestUid,
            HouseholdMatchRejectReq request
    ) {
        // TODO: 수동 거절 처리 로직 구현
        // TODO: 세대 거절 결과 이벤트 Kafka 발행
        return HouseholdMatchRejectRes.builder().rejectedAt(LocalDateTime.now()).build();
    }
}
