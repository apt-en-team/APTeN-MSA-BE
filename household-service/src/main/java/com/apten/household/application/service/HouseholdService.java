package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.household.application.model.request.HouseholdCreateReq;
import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdListReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdPatchReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdCreateRes;
import com.apten.household.application.model.response.HouseholdDetailRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdListRes;
import com.apten.household.application.model.response.HouseholdPatchRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberListRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import com.apten.household.application.model.response.MyHouseholdRes;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.entity.UserCache;
import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.domain.repository.UserCacheRepository;
import com.apten.household.exception.HouseholdErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 세대와 세대원 도메인 API 시그니처를 모아두는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdService {

    // 세대 저장소이다.
    private final HouseholdRepository householdRepository;

    // 세대원 저장소이다.
    private final HouseholdMemberRepository householdMemberRepository;

    // 사용자 캐시 저장소이다.
    private final UserCacheRepository userCacheRepository;

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

    // 세대 정보 수정 서비스이다.
    public HouseholdPatchRes updateHousehold(Long householdId, HouseholdPatchReq request) {
        //TODO 세대 존재 여부 확인
        //TODO building, unit, typeId, status 유효성 검증
        //TODO 세대 기본 정보 수정
        return HouseholdPatchRes.builder()
                .householdId(householdId)
                .building(request.getBuilding())
                .unit(request.getUnit())
                .typeId(request.getTypeId())
                .status(request.getStatus())
                .updatedAt(LocalDateTime.now())
                .build();
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
        Household household = getHouseholdOrThrow(householdId);
        UserCache userCache = getUserCacheOrThrow(request.getUserId());
        HouseholdMemberRole role = request.getRole() != null ? request.getRole() : HouseholdMemberRole.MEMBER;

        // 세대와 사용자 조합 중복 등록을 막는다.
        if (householdMemberRepository.existsByHouseholdIdAndUserId(householdId, request.getUserId())) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_ALREADY_LINKED);
        }

        // 활성 세대주는 한 명만 유지한다.
        validateHeadDuplication(householdId, role, null);

        HouseholdMember householdMember = householdMemberRepository.save(
                HouseholdMember.builder()
                        .householdId(household.getId())
                        .userId(userCache.getId())
                        .role(role)
                        .isActive(true)
                        .build()
        );

        // 세대원 저장과 같은 트랜잭션 안에서 생성 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdMemberCreatedEvent(householdMember);

        return HouseholdMemberPostRes.builder()
                .householdMemberId(householdMember.getId())
                .householdId(householdId)
                .userId(householdMember.getUserId())
                .role(householdMember.getRole())
                .isActive(householdMember.getIsActive())
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
        HouseholdMember householdMember = getHouseholdMemberOrThrow(householdMemberId);
        HouseholdMemberRole nextRole = request.getRole() != null ? request.getRole() : householdMember.getRole();
        Boolean nextIsActive = request.getIsActive() != null ? request.getIsActive() : householdMember.getIsActive();

        validateHeadRemoval(householdMember, nextRole, nextIsActive);
        validateHeadDuplication(householdMember.getHouseholdId(), nextRole, householdMember.getId());

        householdMember.update(nextRole, nextIsActive);
        HouseholdMember savedHouseholdMember = householdMemberRepository.save(householdMember);

        // 세대원 수정과 같은 트랜잭션 안에서 수정 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdMemberUpdatedEvent(savedHouseholdMember);

        return HouseholdMemberPatchRes.builder()
                .householdMemberId(savedHouseholdMember.getId())
                .role(savedHouseholdMember.getRole())
                .isActive(savedHouseholdMember.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 세대원 삭제 서비스이다.
    public HouseholdMemberDeleteRes deleteHouseholdMember(Long householdMemberId) {
        HouseholdMember householdMember = getHouseholdMemberOrThrow(householdMemberId);

        validateHeadRemoval(householdMember, householdMember.getRole(), false);

        // 물리 삭제 대신 비활성 처리 후 removed 이벤트를 적재한다.
        householdMember.changeActive(false);
        HouseholdMember savedHouseholdMember = householdMemberRepository.save(householdMember);

        // 세대원 비활성 처리와 같은 트랜잭션 안에서 제거 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdMemberRemovedEvent(savedHouseholdMember);

        return HouseholdMemberDeleteRes.builder()
                .householdMemberId(savedHouseholdMember.getId())
                .message("세대원 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 세대주 권한 변경 서비스이다.
    public HouseholdHeadPatchRes changeHouseholdHead(Long householdId, HouseholdHeadPatchReq request) {
        getHouseholdOrThrow(householdId);

        HouseholdMember newHead = householdMemberRepository.findByHouseholdIdAndUserIdAndIsActiveTrue(householdId, request.getUserId())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));

        HouseholdMember currentHead = householdMemberRepository.findByHouseholdIdAndRoleAndIsActiveTrue(householdId, HouseholdMemberRole.HEAD)
                .orElse(null);

        if (currentHead != null && !currentHead.getId().equals(newHead.getId())) {
            currentHead.update(HouseholdMemberRole.MEMBER, true);
            HouseholdMember savedCurrentHead = householdMemberRepository.save(currentHead);
            // 기존 세대주의 역할 변경도 별도 이벤트로 반영한다.
            householdOutboxService.saveHouseholdHeadChangedEvent(savedCurrentHead);
        }

        newHead.update(HouseholdMemberRole.HEAD, true);
        HouseholdMember savedNewHead = householdMemberRepository.save(newHead);
        // 신규 세대주의 역할 변경도 별도 이벤트로 반영한다.
        householdOutboxService.saveHouseholdHeadChangedEvent(savedNewHead);

        return HouseholdHeadPatchRes.builder()
                .householdId(householdId)
                .headUserId(savedNewHead.getUserId())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 내 세대 정보 조회 서비스이다.
    public MyHouseholdRes getMyHousehold() {
        //TODO 로그인 사용자 기준 활성 세대원 조회
        //TODO 세대 기본 정보 조회
        //TODO 세대원 이름과 연락처를 user_cache에서 조합
        return MyHouseholdRes.builder()
                .members(List.of())
                .build();
    }

    // 세대 존재 여부를 확인하고 없으면 예외를 던진다.
    private Household getHouseholdOrThrow(Long householdId) {
        return householdRepository.findById(householdId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
    }

    // 사용자 캐시 존재 여부를 확인하고 없으면 예외를 던진다.
    private UserCache getUserCacheOrThrow(Long userId) {
        return userCacheRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.USER_NOT_FOUND));
    }

    // 세대원 존재 여부를 확인하고 없으면 예외를 던진다.
    private HouseholdMember getHouseholdMemberOrThrow(Long householdMemberId) {
        return householdMemberRepository.findById(householdMemberId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
    }

    // 활성 세대주 중복을 막는다.
    private void validateHeadDuplication(Long householdId, HouseholdMemberRole nextRole, Long currentMemberId) {
        if (nextRole != HouseholdMemberRole.HEAD) {
            return;
        }

        householdMemberRepository.findByHouseholdIdAndRoleAndIsActiveTrue(householdId, HouseholdMemberRole.HEAD)
                .filter(existingHead -> currentMemberId == null || !existingHead.getId().equals(currentMemberId))
                .ifPresent(existingHead -> {
                    throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_HEAD_DUPLICATED);
                });
    }

    // 세대주 제거로 인해 세대주가 공백이 되는 상황을 막는다.
    private void validateHeadRemoval(HouseholdMember householdMember, HouseholdMemberRole nextRole, Boolean nextIsActive) {
        if (householdMember.getRole() != HouseholdMemberRole.HEAD) {
            return;
        }

        boolean roleChanged = nextRole != HouseholdMemberRole.HEAD;
        boolean deactivated = !Boolean.TRUE.equals(nextIsActive);

        if (roleChanged || deactivated) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_HEAD_REQUIRED);
        }
    }
}
