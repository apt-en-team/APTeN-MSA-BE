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
import com.apten.household.domain.enums.HouseholdStatus;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.domain.repository.UserCacheRepository;
import com.apten.household.exception.HouseholdErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // 동/호 라인 기준 평형을 해석하는 서비스이다.
    private final HouseholdTypeService householdTypeService;

    // 세대 마스터 등록 서비스이다.
    public HouseholdCreateRes createHousehold(Long complexId, HouseholdCreateReq request) {
        // TODO Gateway Header에서 해석한 complexId를 기준으로 권한과 단지 범위를 최종 검증한다.
        // TODO request.complexId 필드는 하위 호환용으로만 남겨두고 더 이상 신뢰하지 않는다.
        // TODO complex_cache에서 단지 활성 상태를 확인한다.
        if (householdRepository.existsByComplexIdAndBuildingAndUnit(complexId, request.getBuilding(), request.getUnit())) {
            throw new BusinessException(HouseholdErrorCode.DUPLICATE_HOUSEHOLD);
        }

        Household household = householdRepository.save(Household.builder()
                .complexId(complexId)
                .building(request.getBuilding())
                .unit(request.getUnit())
                .typeId(resolveCreateTypeId(complexId, request))
                .status(HouseholdStatus.VACANT)
                .headUserId(null)
                .build());

        // 세대 생성과 같은 트랜잭션 안에서 생성 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdCreatedEvent(household);

        return HouseholdCreateRes.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .status(household.getStatus())
                .createdAt(household.getCreatedAt())
                .build();
    }

    // 세대 목록 조회 서비스이다.
    public HouseholdListRes getHouseholdList(Long complexId, HouseholdListReq request) {
        //TODO Header에서 해석한 complexId 기준으로 동, 호, 상태 조건 조회
        //TODO request.complexId는 더 이상 조회 기준으로 사용하지 않는다.
        //TODO 페이지 메타데이터 계산
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;
        String building = blankToNull(request.getBuilding());
        String unit = blankToNull(request.getUnit());
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Household> page = request.getStatus() == null
                ? householdRepository.findByFilters(complexId, building, unit, pageable)
                : householdRepository.findByFiltersAndStatus(complexId, building, unit, request.getStatus(), pageable);

        return HouseholdListRes.builder()
                .content(page.getContent().stream()
                        .map(this::toHouseholdListItem)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .summary(buildHouseholdSummary(complexId))
                .build();
    }

    // 세대 상세 조회 서비스이다.
    public HouseholdDetailRes getHouseholdDetail(Long complexId, Long householdId) {
        getHouseholdForComplex(complexId, householdId);
        //TODO 세대 기본 정보 조회
        //TODO 세대원 목록 조회
        //TODO 최근 청구 요약 조회
        return HouseholdDetailRes.builder().householdId(householdId).build();
    }

    // 세대 정보 수정 서비스이다.
    public HouseholdPatchRes updateHousehold(Long complexId, Long householdId, HouseholdPatchReq request) {
        Household household = getHouseholdForComplex(complexId, householdId);

        // TODO request의 단지 식별자 값이 있더라도 Header에서 해석한 complexId를 우선 사용한다.
        household.update(
                complexId,
                request.getBuilding() != null ? request.getBuilding() : household.getBuilding(),
                request.getUnit() != null ? request.getUnit() : household.getUnit(),
                request.getTypeId() != null ? request.getTypeId() : household.getTypeId()
        );
        Household savedHousehold = householdRepository.save(household);

        // 세대 기본 정보 수정과 같은 트랜잭션 안에서 수정 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);

        return HouseholdPatchRes.builder()
                .householdId(savedHousehold.getId())
                .building(savedHousehold.getBuilding())
                .unit(savedHousehold.getUnit())
                .typeId(savedHousehold.getTypeId())
                .status(savedHousehold.getStatus())
                .updatedAt(savedHousehold.getUpdatedAt())
                .build();
    }

    // 세대 상태 변경 서비스이다.
    public HouseholdStatusPatchRes changeHouseholdStatus(Long complexId, Long householdId, HouseholdStatusPatchReq request) {
        Household household = getHouseholdForComplex(complexId, householdId);

        // TODO 상태 변경에 따른 세대원/정산 연쇄 처리 정책은 담당자가 후속 구현한다.
        if (request.getStatus() == null) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_STATUS_INVALID);
        }
        household.changeStatus(request.getStatus());
        Household savedHousehold = householdRepository.save(household);

        //TODO household_history 저장
        // 세대 상태 변경도 같은 트랜잭션 안에서 outbox에 적재한다.
        if (request.getStatus() == HouseholdStatus.MOVED_OUT) {
            householdOutboxService.saveHouseholdDeactivatedEvent(savedHousehold);
        } else {
            householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);
        }

        return HouseholdStatusPatchRes.builder()
                .householdId(savedHousehold.getId())
                .status(savedHousehold.getStatus())
                .changedAt(savedHousehold.getUpdatedAt())
                .build();
    }

    // 입주와 퇴거 이력 조회 서비스이다.
    public List<HouseholdHistoryRes> getHouseholdHistory(Long complexId, Long householdId) {
        getHouseholdForComplex(complexId, householdId);
        //TODO 세대 상태 변경 이력 조회
        return List.of();
    }

    // 세대원 등록 서비스이다.
    public HouseholdMemberPostRes addHouseholdMember(Long complexId, Long householdId, HouseholdMemberPostReq request) {
        Household household = getHouseholdForComplex(complexId, householdId);
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
    public List<HouseholdMemberListRes> getHouseholdMembers(Long complexId, Long householdId) {
        getHouseholdForComplex(complexId, householdId);
        //TODO 세대원 목록 조회
        return List.of();
    }

    // 세대원 수정 서비스이다.
    public HouseholdMemberPatchRes updateHouseholdMember(Long complexId, Long householdMemberId, HouseholdMemberPatchReq request) {
        HouseholdMember householdMember = getHouseholdMemberForComplex(complexId, householdMemberId);
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
    public HouseholdMemberDeleteRes deleteHouseholdMember(Long complexId, Long householdMemberId) {
        HouseholdMember householdMember = getHouseholdMemberForComplex(complexId, householdMemberId);

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
    public HouseholdHeadPatchRes changeHouseholdHead(Long complexId, Long householdId, HouseholdHeadPatchReq request) {
        Household household = getHouseholdForComplex(complexId, householdId);

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

        // 세대주가 바뀌면 세대 캐시가 참조하는 headUserId도 함께 갱신한다.
        household.changeHeadUserId(savedNewHead.getUserId());
        Household savedHousehold = householdRepository.save(household);
        householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);

        return HouseholdHeadPatchRes.builder()
                .householdId(householdId)
                .headUserId(savedNewHead.getUserId())
                .updatedAt(savedHousehold.getUpdatedAt())
                .build();
    }

    // 내 세대 정보 조회 서비스이다.
    public MyHouseholdRes getMyHousehold(Long userId, Long complexId) {
        HouseholdMember myMember = householdMemberRepository.findActiveByUserIdAndComplexId(userId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
        Household household = getHouseholdForComplex(complexId, myMember.getHouseholdId());

        List<HouseholdMember> members = householdMemberRepository.findByHouseholdId(household.getId());
        Map<Long, UserCache> userCacheMap = userCacheRepository.findAllById(
                        members.stream()
                                .map(HouseholdMember::getUserId)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(UserCache::getId, Function.identity()));

        return MyHouseholdRes.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .status(household.getStatus())
                .members(members.stream()
                        .map(member -> {
                            UserCache userCache = userCacheMap.get(member.getUserId());
                            return MyHouseholdRes.MemberItem.builder()
                                    .userId(member.getUserId())
                                    .name(userCache != null ? userCache.getName() : null)
                                    .phone(userCache != null ? userCache.getPhone() : null)
                                    .role(member.getRole())
                                    .isActive(member.getIsActive())
                                    .build();
                        })
                        .toList())
                .build();
    }

    // 세대 존재 여부를 확인하고 없으면 예외를 던진다.
    private HouseholdListRes.Item toHouseholdListItem(Household household) {
        String headName = null;
        if (household.getHeadUserId() != null) {
            headName = userCacheRepository.findById(household.getHeadUserId())
                    .map(UserCache::getName)
                    .orElse(null);
        }

        return HouseholdListRes.Item.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .typeId(household.getTypeId())
                .status(household.getStatus())
                .headName(headName)
                .memberCount(householdMemberRepository.countByHouseholdIdAndIsActiveTrue(household.getId()))
                .carCount(0L)
                .createdAt(household.getCreatedAt())
                .build();
    }

    private HouseholdListRes.Summary buildHouseholdSummary(Long complexId) {
        LocalDate now = LocalDate.now();
        LocalDateTime from = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime to = now.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        return HouseholdListRes.Summary.builder()
                .totalHouseholds(householdRepository.countByComplexId(complexId))
                .occupiedHouseholds(householdRepository.countByComplexIdAndStatus(complexId, HouseholdStatus.OCCUPIED))
                .vacantHouseholds(householdRepository.countByComplexIdAndStatus(complexId, HouseholdStatus.VACANT))
                .currentMonthMoveIns(householdRepository.countByComplexIdAndStatusAndUpdatedAtBetween(complexId, HouseholdStatus.OCCUPIED, from, to))
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private Household getHouseholdOrThrow(Long householdId) {
        return householdRepository.findById(householdId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
    }

    // 세대가 현재 단지 소속인지 확인하고 없으면 예외를 던진다.
    private Household getHouseholdForComplex(Long complexId, Long householdId) {
        return householdRepository.findByIdAndComplexId(householdId, complexId)
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

    // 세대원이 현재 단지 소속 세대에 속하는지 확인하고 없으면 예외를 던진다.
    private HouseholdMember getHouseholdMemberForComplex(Long complexId, Long householdMemberId) {
        return householdMemberRepository.findByIdAndComplexId(householdMemberId, complexId)
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

    // 요청에 평형이 없으면 동/호 라인 설정으로 평형을 해석한다.
    private Long resolveCreateTypeId(Long complexId, HouseholdCreateReq request) {
        if (request.getTypeId() != null) {
            return request.getTypeId();
        }
        return householdTypeService.resolveTypeIdOrNull(complexId, request.getBuilding(), request.getUnit());
    }
}
