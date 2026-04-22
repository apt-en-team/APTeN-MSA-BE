package com.apten.household.application.service;

import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdPostReq;
import com.apten.household.application.model.request.HouseholdSearchReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdGetDetailRes;
import com.apten.household.application.model.response.HouseholdGetRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdMemberRes;
import com.apten.household.application.model.response.HouseholdPostRes;
import com.apten.household.application.model.response.PageResponse;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.infrastructure.mapper.HouseholdQueryMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// 세대 도메인 응용 서비스
// 세대 마스터와 세대원 관련 시그니처만 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class HouseholdService {

    // 세대 기본 저장소
    private final HouseholdRepository householdRepository;

    // 세대 도메인 조회용 MyBatis 매퍼
    private final ObjectProvider<HouseholdQueryMapper> householdQueryMapperProvider;

    // 세대 등록 서비스
    public HouseholdPostRes createHousehold(HouseholdPostReq request) {
        // TODO: 세대 마스터 등록 로직 구현
        // TODO: 세대 생성 이벤트 Kafka 발행
        return HouseholdPostRes.builder().createdAt(LocalDateTime.now()).build();
    }

    // 세대 목록 조회 서비스
    public PageResponse<HouseholdGetRes> getHouseholdList(HouseholdSearchReq request) {
        // TODO: 세대 목록 조회 로직 구현
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 세대 상세 조회 서비스
    public HouseholdGetDetailRes getHouseholdDetail(String householdUid) {
        // TODO: 세대 상세 조회 로직 구현
        return HouseholdGetDetailRes.builder().build();
    }

    // 세대 상태 변경 서비스
    public HouseholdStatusPatchRes changeHouseholdStatus(String householdUid, HouseholdStatusPatchReq request) {
        // TODO: 세대 상태 변경 로직 구현
        // TODO: 세대 상태 변경 이벤트 Kafka 발행
        return HouseholdStatusPatchRes.builder().changedAt(LocalDateTime.now()).build();
    }

    // 세대 상태 이력 조회 서비스
    public List<HouseholdHistoryRes> getHouseholdHistory(String householdUid) {
        // TODO: 세대 상태 이력 조회 로직 구현
        return List.of();
    }

    // 세대원 등록 서비스
    public HouseholdMemberPostRes addHouseholdMember(String householdUid, HouseholdMemberPostReq request) {
        // TODO: 세대원 등록 로직 구현
        // TODO: 세대원 변경 이벤트 Kafka 발행
        return HouseholdMemberPostRes.builder().createdAt(LocalDateTime.now()).build();
    }

    // 세대원 목록 조회 서비스
    public List<HouseholdMemberRes> getHouseholdMembers(String householdUid) {
        // TODO: 세대원 목록 조회 로직 구현
        return List.of();
    }

    // 세대원 수정 서비스
    public HouseholdMemberPatchRes updateHouseholdMember(
            String householdMemberUid,
            HouseholdMemberPatchReq request
    ) {
        // TODO: 세대원 수정 로직 구현
        // TODO: 세대원 변경 이벤트 Kafka 발행
        return HouseholdMemberPatchRes.builder().updatedAt(LocalDateTime.now()).build();
    }

    // 세대원 삭제 서비스
    public HouseholdMemberDeleteRes deleteHouseholdMember(String householdMemberUid) {
        // TODO: 세대원 삭제 로직 구현
        // TODO: 세대원 변경 이벤트 Kafka 발행
        return HouseholdMemberDeleteRes.builder().message("세대원 삭제 완료").deletedAt(LocalDateTime.now()).build();
    }

    // 세대주 변경 서비스
    public HouseholdHeadPatchRes changeHouseholdHead(String householdUid, HouseholdHeadPatchReq request) {
        // TODO: 세대주 권한 변경 로직 구현
        // TODO: 세대원 변경 이벤트 Kafka 발행
        return HouseholdHeadPatchRes.builder().updatedAt(LocalDateTime.now()).build();
    }
}
