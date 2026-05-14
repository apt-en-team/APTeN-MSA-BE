package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.exception.HouseholdErrorCode;
import com.apten.household.application.model.request.HouseholdMatchPostReq;
import com.apten.household.application.model.request.HouseholdMatchRejectReq;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchListRes;
import com.apten.household.application.model.request.HouseholdMatchListReq;
import com.apten.household.application.model.response.HouseholdMatchPostRes;
import com.apten.household.application.model.response.HouseholdMatchRejectRes;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 세대 매칭 요청과 승인 처리 시그니처를 모아두는 서비스이다.
@Service
@RequiredArgsConstructor
public class HouseholdMatchService {

    // Outbox 적재 전용 서비스이다.
    private final com.apten.household.infrastructure.kafka.HouseholdOutboxService householdOutboxService;

    // 세대원 저장소이다.
    private final HouseholdMemberRepository householdMemberRepository;

    // 세대 매칭 요청 생성 서비스이다.
    public HouseholdMatchPostRes createMatchRequest(HouseholdMatchPostReq request) {
        // FR-428 이미 세대에 활성 상태로 등록된 사용자의 중복 매칭 요청을 차단한다.
        if (householdMemberRepository.existsByUserIdAndComplexId(
                request.getUserId(), request.getComplexId())) {
            throw new BusinessException(HouseholdErrorCode.ALREADY_HOUSEHOLD_MEMBER);
        }
        //TODO 입력 이름, 전화번호, 생년월일 존재 여부 확인
        //TODO complex_cache에서 단지 존재 여부 확인
        //TODO household와 user_cache 매칭 시도
        //TODO 자동 승인 가능 여부 판단
        //TODO household_match_request 저장
        //TODO 자동 승인 결과 이벤트 outbox 적재 준비
        return HouseholdMatchPostRes.builder()
                .processType("MANUAL")
                .matchStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 수동 승인 대상 조회 서비스이다.
    public HouseholdMatchListRes getMatchRequestList(Long complexId, HouseholdMatchListReq request) {
        //TODO Header에서 해석한 complexId 기준으로 PENDING 상태 수동 승인 대상 조회
        //TODO request.complexId는 더 이상 조회 기준으로 사용하지 않는다.
        //TODO 페이지 메타데이터 계산
        return HouseholdMatchListRes.builder()
                .content(List.of())
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .build();
    }

    // 수동 승인 처리 서비스이다.
    public HouseholdMatchApproveRes approveMatchRequest(Long complexId, Long matchRequestId) {
        //TODO matchRequestId가 현재 complexId 소속인지 검증
        //TODO 매칭 요청 존재 여부 확인
        //TODO 승인 대상 세대 재검증
        //TODO match_status를 APPROVED로 변경
        //TODO processed_at 저장
        //TODO 승인 결과 이벤트 outbox 적재 준비
        return HouseholdMatchApproveRes.builder()
                .matchRequestId(matchRequestId)
                .matchStatus("APPROVED")
                .processedAt(LocalDateTime.now())
                .build();
    }

    // 수동 거절 처리 서비스이다.
    public HouseholdMatchRejectRes rejectMatchRequest(Long complexId, Long matchRequestId, HouseholdMatchRejectReq request) {
        //TODO matchRequestId가 현재 complexId 소속인지 검증
        //TODO 매칭 요청 존재 여부 확인
        //TODO 거절 사유 반영
        //TODO match_status를 REJECTED로 변경
        //TODO processed_at 저장
        //TODO 거절 결과 이벤트 outbox 적재 준비
        return HouseholdMatchRejectRes.builder()
                .matchRequestId(matchRequestId)
                .matchStatus("REJECTED")
                .processedAt(LocalDateTime.now())
                .reason(request.getReason())
                .build();
    }
}
