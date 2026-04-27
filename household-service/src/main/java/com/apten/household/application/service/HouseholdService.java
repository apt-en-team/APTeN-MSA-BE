package com.apten.household.application.service;

import com.apten.household.application.model.request.HouseholdCreateReq;
import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdListReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdCreateRes;
import com.apten.household.application.model.response.HouseholdDetailRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdListRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberListRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 세대와 세대원 도메인 API 시그니처를 모아두는 서비스이다.
@Service
@RequiredArgsConstructor
public class HouseholdService {

    // Outbox 적재 전용 서비스이다.
    private final com.apten.household.infrastructure.kafka.HouseholdOutboxService householdOutboxService;

    // 세대 마스터 등록 서비스이다.
    public HouseholdCreateRes createHousehold(HouseholdCreateReq request) {
        //TODO 세대 중복 여부 확인
        //TODO complex_cache에서 단지 활성 상태 확인
        //TODO household 저장
        //TODO household_history 초기 이력 저장
        //TODO 세대 생성 이벤트 outbox 적재
        return HouseholdCreateRes.builder().createdAt(LocalDateTime.now()).build();
    }

    // 세대 목록 조회 서비스이다.
    public HouseholdListRes getHouseholdList(HouseholdListReq request) {
        //TODO complexId, 동, 호, 상태 조건으로 세대 목록 조회
        //TODO 페이지 메타데이터 계산
        return HouseholdListRes.builder()
                .content(List.of())
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .build();
    }

    // 세대 상세 조회 서비스이다.
    public HouseholdDetailRes getHouseholdDetail(Long householdId) {
        //TODO 세대 기본 정보 조회
        //TODO 세대원 목록 조회
        //TODO 최근 청구 요약 조회
        return HouseholdDetailRes.builder().householdId(householdId).build();
    }

    // 세대 상태 변경 서비스이다.
    public HouseholdStatusPatchRes changeHouseholdStatus(Long householdId, HouseholdStatusPatchReq request) {
        //TODO 세대 존재 여부 확인
        //TODO 세대 상태 유효성 검증
        //TODO 세대 상태 변경
        //TODO household_history 저장
        //TODO 세대 상태 변경 이벤트 outbox 적재
        return HouseholdStatusPatchRes.builder()
                .householdId(householdId)
                .status(request.getStatus())
                .changedAt(LocalDateTime.now())
                .build();
    }

    // 입주와 퇴거 이력 조회 서비스이다.
    public List<HouseholdHistoryRes> getHouseholdHistory(Long householdId) {
        //TODO 세대 상태 변경 이력 조회
        return List.of();
    }

    // 세대원 등록 서비스이다.
    public HouseholdMemberPostRes addHouseholdMember(Long householdId, HouseholdMemberPostReq request) {
        //TODO 세대 존재 여부 확인
        //TODO 사용자 존재 여부 확인
        //TODO 세대원 중복 여부 확인
        //TODO 세대원 저장
        //TODO 세대원 생성 이벤트 outbox 적재
        return HouseholdMemberPostRes.builder()
                .householdId(householdId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 세대원 조회 서비스이다.
    public List<HouseholdMemberListRes> getHouseholdMembers(Long householdId) {
        //TODO 세대원 목록 조회
        return List.of();
    }

    // 세대원 수정 서비스이다.
    public HouseholdMemberPatchRes updateHouseholdMember(Long householdMemberId, HouseholdMemberPatchReq request) {
        //TODO 세대원 존재 여부 확인
        //TODO 세대주 공백 여부 검증
        //TODO 세대원 정보 수정
        //TODO 세대원 수정 이벤트 outbox 적재
        return HouseholdMemberPatchRes.builder()
                .householdMemberId(householdMemberId)
                .role(request.getRole())
                .isActive(request.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 세대원 삭제 서비스이다.
    public HouseholdMemberDeleteRes deleteHouseholdMember(Long householdMemberId) {
        //TODO 세대원 존재 여부 확인
        //TODO 세대주 공백 여부 검증
        //TODO 세대원 비활성 처리
        //TODO 세대원 삭제 이벤트 outbox 적재
        return HouseholdMemberDeleteRes.builder()
                .householdMemberId(householdMemberId)
                .message("세대원 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 세대주 권한 변경 서비스이다.
    public HouseholdHeadPatchRes changeHouseholdHead(Long householdId, HouseholdHeadPatchReq request) {
        //TODO 세대 존재 여부 확인
        //TODO 새 세대주 대상 세대원 확인
        //TODO 기존 세대주와 새 세대주 역할 교체
        //TODO 세대주 변경 이벤트 outbox 적재
        return HouseholdHeadPatchRes.builder()
                .householdId(householdId)
                .headUserId(request.getUserId())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
